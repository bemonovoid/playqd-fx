package io.playqd.controller.view;

import io.playqd.data.Track;
import io.playqd.event.TrackUpdateType;

import java.util.List;

public record TracksUpdate(TrackUpdateType trackUpdateType, List<Track> tracks) {


}
