package io.playqd.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.playqd.client.ClientException;
import io.playqd.client.Images;
import io.playqd.client.PageRequest;
import io.playqd.client.PlayqdClient;
import io.playqd.client.PlayqdClientProvider;
import io.playqd.data.Album;
import io.playqd.data.Artist;
import io.playqd.data.ItemType;
import io.playqd.data.MediaCollection;
import io.playqd.data.MediaItemType;
import io.playqd.data.NewMediaCollectionItem;
import io.playqd.data.Playlist;
import io.playqd.data.Reaction;
import io.playqd.data.Result;
import io.playqd.data.Track;
import io.playqd.data.WatchFolder;
import io.playqd.data.WatchFolderItem;
import io.playqd.data.request.MovePlaylistTracksRequest;
import io.playqd.data.request.UpdateMediaCollectionItemRequest;
import io.playqd.data.request.UpdateMediaCollectionRequest;
import io.playqd.data.request.UpdatePlaylistRequest;
import io.playqd.data.request.UpdateReactionRequest;
import io.playqd.event.LibraryRefreshedEvent;
import io.playqd.player.Player;

public final class MusicLibrary {

    private static final Logger LOG = LoggerFactory.getLogger(MusicLibrary.class);

    private static final SimpleObjectProperty<LibraryRefreshedEvent> LIBRARY_REFRESHED_EVENT_PROPERTY =
            new SimpleObjectProperty<>();

    private static final ObjectProperty<UpdatedTracks> UPDATED_TRACKS_PROPERTY = new SimpleObjectProperty<>();

    private static final ObservableMap<Long, Playlist> PLAYLIST_CACHE =
            FXCollections.observableMap(new HashMap<>());

    private static final ObservableMap<Long, MediaCollection> COLLECTION_CACHE =
            FXCollections.observableMap(new HashMap<>());

    private static final Map<Long, Track> TRACKS_CACHE = Collections.synchronizedMap(new HashMap<>());

    static {
        getAllTracks();
        MusicLibraryScanServiceManager.scanResultProperty().addListener((_, _, scanDataResult) -> {
            scanDataResult.ifPresent(scanData -> {
                if (scanData.added() + scanData.updated() + scanData.deleted() > 0) {
                    refresh();
                }
            });
        });
        Player.onFinished(track -> updatePlayCount(track.id()));
    }

    public static void refresh() {
        LOG.info("Refreshing library caches: collections, playlists, tracks, images ...");
        COLLECTION_CACHE.clear();
        PLAYLIST_CACHE.clear();
        TRACKS_CACHE.clear();
        Images.clearCaches();
        getAllTracks();
        LOG.info("Music library caches were refreshed.");
        LIBRARY_REFRESHED_EVENT_PROPERTY.set(new LibraryRefreshedEvent());
    }

    public static ReadOnlyObjectProperty<LibraryRefreshedEvent> libraryRefreshedEventProperty() {
        return LIBRARY_REFRESHED_EVENT_PROPERTY;
    }

    public static ReadOnlyObjectProperty<UpdatedTracks> updatedTracksProperty() {
        return UPDATED_TRACKS_PROPERTY;
    }

    public static void onPlaylistsModified(Consumer<List<Playlist>> callback) {
        PLAYLIST_CACHE.addListener((MapChangeListener<? super Long, ? super Playlist>) _ ->
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
                .sorted(TrackComparators.byAlbumAndTrackNumber())
                .toList());
    }

    public static List<Track> getAlbumTracks(long albumTrackId) {
        var albumTrack = getTrackById(albumTrackId);
        return new ArrayList<>(getAllTracksStreamExcludingCueParent()
                .filter(track -> track.artistName().equals(albumTrack.artistName()))
                .filter(track -> track.albumName().equals(albumTrack.albumName()))
                .sorted(TrackComparators.trackInstanceNumberComparator())
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

    public static List<Track> getReactedTracks(Reaction reaction) {
        return new ArrayList<>(getAllTracksStreamExcludingCueParent()
                .filter(t -> Reaction.NONE != reaction && t.reaction() == reaction)
                .sorted(TrackComparators.byReactionDate().reversed())
                .toList());
    }

    public static void updateReaction(List<Long> trackIds, Reaction reaction) {
        var updatedTracks = playqdClient().updateReaction(new UpdateReactionRequest(reaction, trackIds));
        updateTrackInCache(updatedTracks);
    }

    public static void updatePlayCount(long trackId) {
        var trackUpdated = playqdClient().markAsPlayed(trackId);
        updateTrackInCache(List.of(trackUpdated));
    }

    public static List<Playlist> getPlaylists() {
        if (PLAYLIST_CACHE.isEmpty()) {
            PLAYLIST_CACHE.putAll(playlistApi().getAll().stream()
                    .collect(Collectors.toMap(Playlist::id, p -> p)));
        }
        return new ArrayList<>(PLAYLIST_CACHE.values());
    }

    public static List<Playlist> findPlaylistsWithTrackId(long trackId) {
        return getPlaylists().stream().filter(p -> p.tracks().stream().anyMatch(t -> t.id() == trackId)).toList();
    }

    public static Playlist createPlaylist(String name) {
        return createPlaylist(name, List.of());
    }

    public static Playlist createPlaylist(String name, List<Long> trackIds) {
        var playlist = playlistApi().create(name, trackIds);
        PLAYLIST_CACHE.put(playlist.id(), playlist);
        return playlist;
    }

    public static Playlist getPlaylist(long id) {
        return PLAYLIST_CACHE.computeIfAbsent(id, _ -> playlistApi().get(id));
    }

    public static Playlist addTracksToPlaylist(long id, List<Long> trackIds) {
        var playlist = playlistApi().addTracks(id, trackIds);
        PLAYLIST_CACHE.put(playlist.id(), playlist);
        return playlist;
    }

    public static void movePlaylistTracks(long fromPlaylistId, long toPlaylistId, List<Long> trackIds) {
        playlistApi().moveTracks(new MovePlaylistTracksRequest(fromPlaylistId, toPlaylistId, trackIds));
        PLAYLIST_CACHE.put(fromPlaylistId, playlistApi().get(fromPlaylistId));
        PLAYLIST_CACHE.put(toPlaylistId, playlistApi().get(toPlaylistId));
    }

    public static Playlist removeTracksFromPlaylist(long id, List<Long> trackIds) {
        var playlist = playlistApi().removeTracks(id, trackIds);
        PLAYLIST_CACHE.put(playlist.id(), playlist);
        return playlist;
    }

    public static MediaCollection removeTracksFromCollection(long id, List<Long> ids) {
        var collection = playqdClient().collections().removeItems(id, ids);
        COLLECTION_CACHE.put(collection.id(), collection);
        return collection;
    }

    public static void deletePlaylist(long id) {
        playlistApi().delete(id);
        PLAYLIST_CACHE.remove(id);
    }

    public static void deletePlaylists(List<Long> ids) {
        playlistApi().delete(ids);
        ids.forEach(PLAYLIST_CACHE::remove);
    }

    public static Playlist updatePlaylist(long id, String newName) {
        var updated = playlistApi().update(new UpdatePlaylistRequest(id, newName));
        PLAYLIST_CACHE.put(updated.id(), updated);
        return updated;
    }

    public static List<MediaCollection> getCollections() {
        if (COLLECTION_CACHE.isEmpty()) {
            COLLECTION_CACHE.putAll(collectionsApi().getAll().stream()
                    .collect(Collectors.toMap(MediaCollection::id, p -> p)));
        }
        return new ArrayList<>(COLLECTION_CACHE.values());
    }

    public static MediaCollection getCollection(long id) {
        return COLLECTION_CACHE.computeIfAbsent(id, _ -> collectionsApi().get(id));
    }

    public static List<MediaCollection> findCollectionsWithTrackId(long id) {
        return getCollections().stream()
                .filter(c -> c.items().stream().
                        anyMatch(i -> MediaItemType.TRACK == i.itemType() && i.refId().equals("" + id)))
                .toList();
    }

    public static MediaCollection createCollection(String name) {
        return createCollection(name, List.of());
    }

    public static MediaCollection createCollection(String name, List<NewMediaCollectionItem> items) {
        var collection = collectionsApi().create(name, items);
        COLLECTION_CACHE.put(collection.id(), collection);
        return collection;
    }

    public static MediaCollection updateCollection(long id, String newName) {
        var updated = collectionsApi().update(new UpdateMediaCollectionRequest(id, newName));
        COLLECTION_CACHE.put(updated.id(), updated);
        return updated;
    }

    public static void updateCollectionItemComment(long id, String comment) {
        var request = new UpdateMediaCollectionItemRequest(comment);
        var updated = collectionsApi().updateItem(id, request);
        COLLECTION_CACHE.put(updated.id(), updated);
    }

    public static MediaCollection addItemsToCollection(long id, List<NewMediaCollectionItem> items) {
        var collection = collectionsApi().addItems(id, items);
        COLLECTION_CACHE.put(id, collection);
        return collection;
    }

    public static void deleteCollectionItems(long id, List<Long> itemIds) {
        collectionsApi().deleteItems(itemIds);
        COLLECTION_CACHE.put(id, collectionsApi().get(id));
    }

    public static void deleteCollection(long id) {
        collectionsApi().delete(id);
        COLLECTION_CACHE.remove(id);
    }

    // Watch Folders

    public static List<WatchFolder> getWatchFolders() {
        return watchFoldersApi().getAll();
    }

    public static List<WatchFolderItem> getWatchFolderItems(String parentId) {
        return watchFoldersApi().getChildrenItems(parentId);
    }

    public static WatchFolderItem getWatchFolderItemByLocation(String location) {
        return watchFoldersApi().geItemByLocation(location);
    }

    public static List<WatchFolderItem> getWatchFolderItemsByLocation(String location, ItemType itemType) {
        return watchFoldersApi().getChildrenItemsByLocation(location, itemType);
    }

    private static void updateTrackInCache(List<Track> tracks) {
        tracks.forEach(track -> TRACKS_CACHE.put(track.id(), track));
        setUpdatedTracksProperty(tracks);
    }

    private static void setUpdatedTracksProperty(List<Track> tracks) {
        UPDATED_TRACKS_PROPERTY.set(new UpdatedTracks(tracks));
    }

    private static Map<Long, Track> getTracksFromCache() {
        if (TRACKS_CACHE.isEmpty()) {
            LOG.info("Music library cache is empty, retrieving item from server ...");
            var tracksFromServer = getTracksFromServer().stream()
                    .collect(Collectors.toMap(Track::id, Function.identity()));
            LOG.info("Retrieved and cached {} tracks from server", tracksFromServer.size());
            TRACKS_CACHE.putAll(tracksFromServer);
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
                albumTrack.fileAttributes().createdDate().toLocalDate(),
                tracks.size(),
                tracks.stream().mapToInt(t -> t.length().seconds()).sum()
        );
    }

    private static PlayqdClient playqdClient() {
        return PlayqdClientProvider.get();
    }

    private static PlayqdClient.PlaylistApi playlistApi() {
        return playqdClient().playlists();
    }

    private static PlayqdClient.CollectionsApi collectionsApi() {
        return playqdClient().collections();
    }

    private static PlayqdClient.WatchFoldersApi watchFoldersApi() {
        return playqdClient().watchFolders();
    }

    private MusicLibrary() {

    }
}
