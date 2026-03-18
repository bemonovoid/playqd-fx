package io.playqd.player;

import io.playqd.data.Track;

import java.util.List;

public record TrackListRequest(List<Track> tracks, int startTrackIdx) {

    public TrackListRequest(Track track) {
        this(List.of(track));
    }

    public TrackListRequest(List<Track> tracks) {
        this(tracks, 0);
    }
}
