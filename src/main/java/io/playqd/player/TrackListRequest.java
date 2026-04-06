package io.playqd.player;

import io.playqd.data.Track;

import java.util.List;

public record TrackListRequest(int firstTrackPosition, List<Track> tracks, boolean autoPlay) {

    public TrackListRequest(int firstTrackPosition, List<Track> tracks) {
        this(firstTrackPosition, tracks, true);
    }

    public TrackListRequest(Track track) {
        this(List.of(track));
    }

    public TrackListRequest(List<Track> tracks) {
        this(0, tracks);
    }
}
