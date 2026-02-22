package io.playqd.service;

import io.playqd.client.PageRequest;
import io.playqd.client.PlayqdClientProvider;
import io.playqd.data.Track;

import java.util.ArrayList;
import java.util.List;

public final class TracksService {

    public static void addToFavorites(String trackId) {
        PlayqdClientProvider.get().addToFavorites(trackId);
    }

    public static void removeFromFavorites(String trackId) {
        PlayqdClientProvider.get().removeFromFavorites(trackId);
    }

    public static List<Track> getAllTracks() {
        return new ArrayList<>(PlayqdClientProvider.get().getAllTracks(PageRequest.unpaged()).content());
    }

    public static List<Track> getAllFavorites() {
        return PlayqdClientProvider.get().getFavoriteTracks();
    }

    private TracksService() {

    }
}
