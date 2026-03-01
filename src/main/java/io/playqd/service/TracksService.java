package io.playqd.service;

import io.playqd.client.PageRequest;
import io.playqd.client.PlayqdClientProvider;
import io.playqd.data.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class TracksService {

    private static Map<Long, Track> TRACKS_CACHE;

    public static void addToFavorites(long trackId) {
        PlayqdClientProvider.get().addToFavorites(trackId);
    }

    public static void removeFromFavorites(long trackId) {
        PlayqdClientProvider.get().removeFromFavorites(trackId);
    }

    public static Track getTrackById(long id) {
        return TRACKS_CACHE.get(id);
    }

    public static List<Track> getAllTracks() {
        return getTracksFromCache().values().stream()
                .filter(t -> t.cueInfo().cueFile() == null)
                .toList();
    }

    public static List<Track> getCueTracks() {
        return getTracksFromCache().values().stream().filter(t -> t.cueInfo().parentId() != null).toList();
    }

    public static List<Track> getFavorites() {
        return getTracksFromCache().values().stream()
                .filter(t -> t.cueInfo().cueFile() == null) // includes only cue tracks
                .filter(t -> t.rating() != null)
                .filter(t -> t.rating().value() > 0)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static Map<Long, Track> getTracksFromCache() {
        if (TRACKS_CACHE == null || TRACKS_CACHE.isEmpty()) {
            return TRACKS_CACHE = getTracksFromServer().stream()
                    .collect(Collectors.toMap(Track::id, Function.identity()));
        }
        return TRACKS_CACHE;
    }

    private static List<Track> getTracksFromServer() {
        return new ArrayList<>(PlayqdClientProvider.get().getAllTracks(PageRequest.unpaged()).content());
    }

    private TracksService() {

    }
}
