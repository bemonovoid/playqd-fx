package io.playqd.controller.tracks;

import io.playqd.data.Track;
import io.playqd.event.TrackUpdateType;

import java.util.List;

public record TracksUpdate(TrackUpdateType trackUpdateType, List<Track> tracks) {


}
