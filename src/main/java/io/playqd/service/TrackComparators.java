package io.playqd.service;

import io.playqd.data.Track;

import java.util.Comparator;

public final class TrackComparators {

    public static Comparator<Track> byRatedDate() {
        return (t1, t2) -> {
            if (t1.rating().ratedDate() != null && t2.rating().ratedDate() != null) {
                return t1.rating().ratedDate().compareTo(t2.rating().ratedDate());
            }
            return 0;
        };
    }

    public static Comparator<Track> byLastPlayedDate(boolean reverse) {
        return (t1, t2) -> {
            if (t1.playback().lastPlayedDate() != null && t2.playback().lastPlayedDate() != null) {
                if (reverse) {
                    return t2.playback().lastPlayedDate().compareTo(t1.playback().lastPlayedDate());
                }
                return t1.playback().lastPlayedDate().compareTo(t2.playback().lastPlayedDate());
            }
            return 0;
        };
    }

    private TrackComparators() {

    }
}
