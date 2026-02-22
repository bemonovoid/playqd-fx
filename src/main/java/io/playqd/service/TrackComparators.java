package io.playqd.service;

import io.playqd.data.Track;

import java.util.Comparator;

public final class TrackComparators {

    public static Comparator<Track> byFavoriteAddedDateDesc() {
        return byFavoriteAddedDate(true);
    }

    public static Comparator<Track> byFavoriteAddedDate(boolean reverse) {
        return (t1, t2) -> {
            if (t1.rating().ratedDate() != null && t2.rating().ratedDate() != null) {
                if (reverse) {
                    return t2.rating().ratedDate().compareTo(t1.rating().ratedDate());
                }
                return t1.rating().ratedDate().compareTo(t2.rating().ratedDate());
            }
            return 0;
        };
    }

    private TrackComparators() {

    }
}
