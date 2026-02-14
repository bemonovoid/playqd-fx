package io.playqd.controller.music;

import io.playqd.data.Track;

public record TrackTableRow(Track track, boolean artistHeader, boolean albumHeader) {

    public TrackTableRow(Track track) {
        this(track, false, false);
    }
}
