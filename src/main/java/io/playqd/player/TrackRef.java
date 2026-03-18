package io.playqd.player;

import io.playqd.data.Track;
import io.playqd.utils.PlayqdApis;

record TrackRef(String mrl, String[] options, Track track) {

    TrackRef(Track track) {
        this(resolveMrl(track), resolveOptions(track), track);
    }

    private static String resolveMrl(Track track) {
        var trackId = track.cueInfo().parentId() != null ? track.cueInfo().parentId(): track.id();
        return PlayqdApis.trackStream(trackId);
    }

    private static String[] resolveOptions(Track track) {
        var options = new String[]{};
        if (track.cueInfo().parentId() != null) {
            options = buildRangeOptions(track);
        }
        return options;
    }

    private static String[] buildRangeOptions(Track track) {
        var startTime = track.cueInfo().startTimeInSeconds();
        var endTime = track.cueInfo().startTimeInSeconds() + track.length().seconds();
        var startTimeOption = ":start-time=" + startTime;
        var stopTimeOption = ":stop-time=" + endTime;
        return new String[]{ startTimeOption, stopTimeOption };
    }

}
