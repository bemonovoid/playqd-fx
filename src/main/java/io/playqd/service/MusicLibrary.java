package io.playqd.service;

import io.playqd.client.ClientException;
import io.playqd.client.PageRequest;
import io.playqd.client.PlayqdClient;
import io.playqd.client.PlayqdClientProvider;
import io.playqd.data.*;
import io.playqd.data.request.MovePlaylistTracksRequest;
import io.playqd.data.request.UpdateMediaCollectionRequest;
import io.playqd.data.request.UpdatePlaylistRequest;
import io.playqd.event.LibraryRefreshedEvent;
import io.playqd.event.TrackUpdateType;
import io.playqd.event.TracksUpdatedEvent;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MusicLibrary {

    private static final Logger LOG = LoggerFactory.getLogger(MusicLibrary.class);

    private static final SimpleObjectProperty<LibraryRefreshedEvent> LIBRARY_REFRESHED_EVENT_PROPERTY =
            new SimpleObjectProperty<>();

    private static final SimpleObjectProperty<TracksUpdatedEvent> TRACKS_UPDATED_EVENT_PROPERTY =
            new SimpleObjectProperty<>();

    private static final ObservableMap<Long, PlaylistWithTrackIds> PLAYLIST_CACHE =
            FXCollections.observableMap(new HashMap<>());

    private static final ObservableMap<Long, MediaCollection> COLLECTION_CACHE =
            FXCollections.observableMap(new HashMap<>());

    private static Map<Long, Track> TRACKS_CACHE;

    static {
        MusicLibraryScanServiceManager.scanResultProperty().addListener((_, _, scanDataResult) -> {
            scanDataResult.ifPresent(scanData -> {
                if (scanData.added() + scanData.updated() + scanData.deleted() > 0) {
                    refresh();
                }
            });
        });
    }

    public static void refresh() {
        LOG.info("Refreshing library cache ...");
        var itemsBefore = TRACKS_CACHE.size();
        TRACKS_CACHE.clear();
        getTracksFromCache();
        PLAYLIST_CACHE.clear();
        COLLECTION_CACHE.clear();
        LIBRARY_REFRESHED_EVENT_PROPERTY.set(new LibraryRefreshedEvent());
        LOG.info("Music library cache was refreshed. Items before: {}, items after: {}",
                itemsBefore, TRACKS_CACHE.size());
    }

    public static ReadOnlyObjectProperty<LibraryRefreshedEvent> libraryRefreshedEventProperty() {
        return LIBRARY_REFRESHED_EVENT_PROPERTY;
    }

    public static ReadOnlyObjectProperty<TracksUpdatedEvent> tracksUpdatedEventProperty() {
        return TRACKS_UPDATED_EVENT_PROPERTY;
    }

    public static void onPlaylistsModified(Consumer<List<PlaylistWithTrackIds>> callback) {
        PLAYLIST_CACHE.addListener((MapChangeListener<? super Long, ? super PlaylistWithTrackIds>) _ ->
                callback.accept(new ArrayList<>(PLAYLIST_CACHE.values())));
    }

    public static void onCollectionsModified(Consumer<List<MediaCollection>> callback) {
        COLLECTION_CACHE.addListener((MapChangeListener<? super Long, ? super MediaCollection>) _ ->
                callback.accept(new ArrayList<>(COLLECTION_CACHE.values())));
    }

    public static List<Artist> getArtists() {
        return getAllTracksStreamExcludingCueParent()
                .collect(Collectors.groupingBy(Track::artistName)).values().stream()
                .map(artistTracks -> {
                    var track = artistTracks.getFirst();
                    return new Artist(
                            track.id(),
                            track.artistName(),
                            (int) artistTracks.stream().map(Track::albumName).distinct().count(),
                            artistTracks.size());
                })
                .toList();
    }

    public static List<Album> getAllAlbums() {
        return getAllTracksStreamExcludingCueParent()
                .collect(Collectors.groupingBy(t -> t.artistName() + "-" + t.albumName())).values().stream()
                .map(MusicLibrary::tracksToAlbum)
                .toList();
    }

    public static List<Album> getArtistAlbums(long artistTrackId) {
        var artistTrack = getTrackById(artistTrackId);
        var tracksByAlbum = getAllTracksStreamExcludingCueParent()
                .filter(track -> track.artistName().equals(artistTrack.artistName()))
                .collect(Collectors.groupingBy(Track::albumName));

        return tracksByAlbum.values().stream().map(MusicLibrary::tracksToAlbum).toList();
    }

    public static Track getTrackById(long id) {
        return TRACKS_CACHE.computeIfAbsent(id, MusicLibrary::getTrackFromServer);
    }

    public static Result<Track> findTrackById(long id) {
        try {
            return Result.success(TRACKS_CACHE.computeIfAbsent(id, MusicLibrary::getTrackFromServer));
        } catch (ClientException e) {
            return Result.error(e);
        }
    }

    public static List<Track> getTracksById(Collection<Long> ids) {
        return getTracksById(ids, new TrackFilters());
    }
    
    public static List<Track> getTracksById(Collection<Long> ids, TrackFilters filters) {
        var trackIds = new HashSet<>(ids);
        return getAllTracks().stream()
                .filter(t -> trackIds.contains(t.id()))
                .toList();
    }

    public static List<Track> getTracksByPaths(Set<Path> paths) {
        return getAllTracksStreamExcludingCueChildren()
                .filter(t -> paths.contains(Paths.get(t.fileAttributes().location())))
                .toList();
    }

    public static List<Track> getAllTracks() {
        return new ArrayList<>(getTracksFromCache().values());
    }

    public static List<Track> getAllTracksExcludingCueParent() {
        return new ArrayList<>(getAllTracksStreamExcludingCueParent().toList());
    }

    private static Stream<Track> getAllTracksStreamExcludingCueParent() {
        return getAllTracks().stream().filter(t -> !t.isCueParentTrack());
    }

    private static Stream<Track> getAllTracksStreamExcludingCueChildren() {
        return getAllTracks().stream().filter(t -> !t.isCueTrack());
    }

    public static List<Track> getArtistTracks(long artistTrackId) {
        var artistTrack = getTrackById(artistTrackId);
        return new ArrayList<>(getAllTracksStreamExcludingCueParent()
                .filter(track -> track.artistName().equals(artistTrack.artistName()))
                .toList());
    }

    public static List<Track> getAlbumTracks(long albumTrackId) {
        var albumTrack = getTrackById(albumTrackId);
        return new ArrayList<>(getAllTracksStreamExcludingCueParent()
                .filter(track -> track.artistName().equals(albumTrack.artistName()))
                .filter(track -> track.albumName().equals(albumTrack.albumName()))
                .toList());
    }

    public static List<Track> getPlayedTracks() {
        return new ArrayList<>(getAllTracksStreamExcludingCueParent()
                .filter(t -> Objects.nonNull(t.playback()))
                .filter(t -> t.playback().count() > 0)
                .sorted(Comparator.comparing(t -> t.playback().lastPlayedDate()))
                .toList()
                .reversed());
    }

    public static List<Track> getCueTracks() {
        return new ArrayList<>(getTracksFromCache().values().stream()
                .filter(Track::isCueTrack)
                .toList());
    }

    public static List<Track> getFavoriteTracks() {
        return new ArrayList<>(getAllTracksStreamExcludingCueParent()
                .filter(t -> t.rating() != null)
                .filter(t -> t.rating().value() > 0)
                .sorted(TrackComparators.byRatedDate())
                .toList());
    }

    public static void like(List<Long> trackIds) {
        var tracksUpdated = playqdClient().like(trackIds);
        updateTrackInCache(tracksUpdated);
        updateTracksUpdateEventProperty(TrackUpdateType.LIKED, tracksUpdated);
    }

    public static void unlike(List<Long> trackIds) {
        var tracksUpdated = playqdClient().unLike(trackIds);
        updateTrackInCache(tracksUpdated);
        updateTracksUpdateEventProperty(TrackUpdateType.UNLIKED, tracksUpdated);
    }

    public static void markAsPlayed(long trackId) {
        var trackUpdated = playqdClient().markAsPlayed(trackId);
        updateTrackInCache(List.of(trackUpdated));
    }

    public static List<PlaylistWithTrackIds> getPlaylists() {
        if (PLAYLIST_CACHE.isEmpty()) {
            PLAYLIST_CACHE.putAll(playqdClient().getPlaylists().stream()
                    .collect(Collectors.toMap(PlaylistWithTrackIds::id, p -> p)));
        }
        return new ArrayList<>(PLAYLIST_CACHE.values());
    }

    public static PlaylistWithTrackIds createPlaylist(String name) {
        return createPlaylist(name, List.of());
    }

    public static PlaylistWithTrackIds createPlaylist(String name, List<Long> trackIds) {
        var playlist = playqdClient().createPlaylist(name, trackIds);
        PLAYLIST_CACHE.put(playlist.id(), playlist);
        return playlist;
    }

    public static PlaylistWithTrackIds addTracksToPlaylist(long id, List<Long> trackIds) {
        var playlist = playqdClient().addTracksToPlaylist(id, trackIds);
        PLAYLIST_CACHE.put(playlist.id(), playlist);
        return playlist;
    }

    public static void movePlaylistTracks(long fromPlaylistId, long toPlaylistId, List<Long> trackIds) {
        playqdClient().movePlaylistAudioTracks(new MovePlaylistTracksRequest(fromPlaylistId, toPlaylistId, trackIds));
        PLAYLIST_CACHE.put(fromPlaylistId, playqdClient().getPlaylist(fromPlaylistId));
        PLAYLIST_CACHE.put(toPlaylistId, playqdClient().getPlaylist(toPlaylistId));
    }

    public static PlaylistWithTrackIds removeTracksFromPlaylist(long id, List<Long> trackIds) {
        var playlist = playqdClient().removeTracksFromPlaylist(id, trackIds);
        PLAYLIST_CACHE.put(playlist.id(), playlist);
        return playlist;
    }

    public static void deletePlaylist(long id) {
        playqdClient().deletePlaylist(id);
        PLAYLIST_CACHE.remove(id);
    }

    public static void deletePlaylists(List<Long> ids) {
        playqdClient().deletePlaylists(ids);
        ids.forEach(PLAYLIST_CACHE::remove);
    }

    public static PlaylistWithTrackIds updatePlaylist(long id, String newName) {
        var updated = playqdClient().updatePlaylist(new UpdatePlaylistRequest(id, newName));
        PLAYLIST_CACHE.put(updated.id(), updated);
        return updated;
    }

    public static List<MediaCollection> getCollections() {
        if (COLLECTION_CACHE.isEmpty()) {
            COLLECTION_CACHE.putAll(playqdClient().getCollections().stream()
                    .collect(Collectors.toMap(MediaCollection::id, p -> p)));
        }
        return new ArrayList<>(COLLECTION_CACHE.values());
    }

    public static MediaCollection createCollection(String name, List<MediaCollectionItem> items) {
        var collection = playqdClient().createCollection(name, items);
        COLLECTION_CACHE.put(collection.id(), collection);
        return collection;
    }

    public static MediaCollection updateCollection(long id, String newName) {
        var updated = playqdClient().updateCollection(new UpdateMediaCollectionRequest(id, newName));
        COLLECTION_CACHE.put(updated.id(), updated);
        return updated;
    }

    public static MediaCollection addItemsToCollection(long id, List<MediaCollectionItem> items) {
        var collection = playqdClient().addItems(id, items);
        COLLECTION_CACHE.put(id, collection);
        return collection;
    }

    public static void deleteCollection(long id) {
        playqdClient().deleteCollection(id);
        COLLECTION_CACHE.remove(id);
    }

    private static void updateTrackInCache(List<Track> tracks) {
        tracks.forEach(track -> TRACKS_CACHE.put(track.id(), track));
    }

    private static void updateTracksUpdateEventProperty(TrackUpdateType trackUpdateType, List<Track> tracks) {
        TRACKS_UPDATED_EVENT_PROPERTY.set(new TracksUpdatedEvent(trackUpdateType, tracks));
    }

    private static Map<Long, Track> getTracksFromCache() {
        if (TRACKS_CACHE == null || TRACKS_CACHE.isEmpty()) {
            LOG.info("Music library cache is empty, retrieving item from server ...");
            return TRACKS_CACHE = getTracksFromServer().stream()
                    .collect(Collectors.toMap(Track::id, Function.identity()));
        }
        return TRACKS_CACHE;
    }

    private static List<Track> getTracksFromServer() {
        return new ArrayList<>(playqdClient().getAllTracks(PageRequest.unpaged()).content());
    }

    private static Track getTrackFromServer(long id) {
        return playqdClient().getTrackById(id);
    }

    private static Album tracksToAlbum(List<Track> tracks) {
        var albumTrack = tracks.getFirst();
        return new Album(
                albumTrack.id(),
                albumTrack.albumName(),
                albumTrack.releaseDate(),
                albumTrack.genre(),
                albumTrack.artistName(),
                albumTrack.additionalInfo().addedToWatchFolderDate().toLocalDate(),
                tracks.size(),
                tracks.stream().mapToInt(t -> t.length().seconds()).sum()
        );
    }

    private static PlayqdClient playqdClient() {
        return PlayqdClientProvider.get();
    }

    private MusicLibrary() {

    }
}
