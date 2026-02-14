package io.playqd.player;

import io.playqd.data.Track;

import java.util.List;

public record PlayRequest(List<Track> tracks, int startTrackIdx) {

    public PlayRequest(Track track) {
        this(List.of(track));
    }

    public PlayRequest(List<Track> tracks) {
        this(tracks, 0);
    }
}
