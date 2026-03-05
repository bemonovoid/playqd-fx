package io.playqd.service;

import io.playqd.client.PageRequest;
import io.playqd.client.PlayqdClientProvider;
import io.playqd.data.Album;
import io.playqd.data.Artist;
import io.playqd.data.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MusicLibrary {

    private static final Logger LOG = LoggerFactory.getLogger(MusicLibrary.class);

    private static Map<Long, Track> TRACKS_CACHE;

    public static void refresh() {
        LOG.info("Refreshing library cache ...");
        var itemsBefore = TRACKS_CACHE.size();
        TRACKS_CACHE.clear();
        getTracksFromCache();
        LOG.info("Music library cache was refreshed. Items before: {}, items after: {}",
                itemsBefore, TRACKS_CACHE.size());
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

    public static List<Track> getAllTracks() {
        return new ArrayList<>(getTracksFromCache().values());
    }

    public static List<Track> getAllTracksExcludingCueParent() {
        return new ArrayList<>(getAllTracksStreamExcludingCueParent().toList());
    }

    private static Stream<Track> getAllTracksStreamExcludingCueParent() {
        return getAllTracks().stream().filter(t -> t.cueInfo().cueFile() == null);
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

    public static List<Track> getCueTracks() {
        return new ArrayList<>(getTracksFromCache().values().stream()
                .filter(t -> t.cueInfo().parentId() != null)
                .toList());
    }

    public static List<Track> getFavoriteTracks() {
        return new ArrayList<>(getAllTracksStreamExcludingCueParent()
                .filter(t -> t.rating() != null)
                .filter(t -> t.rating().value() > 0)
                .toList());
    }

    public static void addToFavorites(long trackId) {
        PlayqdClientProvider.get().addToFavorites(trackId);
        TRACKS_CACHE.put(trackId, getTrackFromServer(trackId));
    }

    public static void removeFromFavorites(long trackId) {
        PlayqdClientProvider.get().removeFromFavorites(trackId);
        TRACKS_CACHE.put(trackId, getTrackFromServer(trackId));
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
        return new ArrayList<>(PlayqdClientProvider.get().getAllTracks(PageRequest.unpaged()).content());
    }

    private static Track getTrackFromServer(long id) {
        return PlayqdClientProvider.get().getTrackById(id);
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

    private MusicLibrary() {

    }
}
