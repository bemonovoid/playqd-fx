package io.playqd.player;

import io.playqd.data.Track;
import io.playqd.utils.PlayqdApis;

public record TrackRef(String mrl, String[] options, Track track) {

    TrackRef(Track track) {
        this(PlayqdApis.trackStream(track.id()), resolveOptions(track), track);
    }

    private static String[] resolveOptions(Track track) {
        var options = new String[]{};
        if (track.isCueTrack()) {
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
