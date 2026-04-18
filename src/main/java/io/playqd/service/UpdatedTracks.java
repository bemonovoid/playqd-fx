package io.playqd.service;

import io.playqd.data.Track;

import java.util.List;

public final class UpdatedTracks {

    private final List<Track> tracks;
    private final long timestamp;

    public UpdatedTracks(List<Track> tracks) {
        this.tracks = tracks;
        this.timestamp = System.currentTimeMillis();
    }

    public List<Track> tracks() {
        return tracks;
    }
}
