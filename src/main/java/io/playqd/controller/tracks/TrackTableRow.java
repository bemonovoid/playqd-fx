package io.playqd.controller.tracks;

import io.playqd.data.Track;

public record TrackTableRow(Track track, boolean artistHeader, boolean albumHeader) {

    public TrackTableRow(Track track) {
        this(track, false, false);
    }
}
