package io.playqd.service;

import io.playqd.data.Track;

import java.util.Comparator;

public final class TrackComparators {

    public static Comparator<Track> trackInstanceNumberComparator() {
        return (t1, t2) -> {
            try {
                if (t1.number() == null || t2.number() == null) {
                    return 0;
                }
                return Integer.compare(Integer.parseInt(t1.number()), Integer.parseInt(t2.number()));
            } catch (NumberFormatException e) {
                return t1.number().compareTo(t2.number());
            }
        };
    }

    public static Comparator<String> trackNumberComparator() {
        return (n1, n2) -> {
            try {
                if (n1 == null || n2 == null) {
                    return 0;
                }
                return Integer.compare(Integer.parseInt(n1), Integer.parseInt(n2));
            } catch (NumberFormatException e) {
                return n1.compareTo(n2);
            }
        };
    }

    public static Comparator<Track> byReactionDate() {
        return (t1, t2) -> {
            if (t1.reactionDate() != null && t2.reactionDate() != null) {
                return t1.reactionDate().compareTo(t2.reactionDate());
            }
            return 0;
        };
    }

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
