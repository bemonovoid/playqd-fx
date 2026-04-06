package io.playqd.service;

import io.playqd.data.Track;

import java.util.Comparator;

public final class TrackComparators {

    public static Comparator<Track> trackInstanceNumberComparator() {
        return (t1, t2) -> {
            var t1Num = t1.number() == null ? "0" : t1.number();
            var t2Num = t2.number() == null ? "0" : t2.number();
            if (!t1Num.equals("0") && t1Num.length() == 1) {
                t1Num = "0" +  t1Num;
            }
            if (!t2Num.equals("0") && t2Num.length() == 1) {
                t2Num = "0" +  t2Num;
            }
            return t1Num.compareTo(t2Num);
        };
    }

    public static Comparator<Track> byAlbumAndTrackNumber() {
        return (t1, t2) -> {
            var t1Num = t1.number() == null ? "0" : t1.number();
            var t2Num = t2.number() == null ? "0" : t2.number();
            if (!t1Num.equals("0") && t1Num.length() == 1) {
                t1Num = "0" +  t1Num;
            }
            if (!t2Num.equals("0") && t2Num.length() == 1) {
                t2Num = "0" +  t2Num;
            }
            var v1 = t1.albumName() + t1Num;
            var v2 = t2.albumName() + t2Num;
            return v1.compareTo(v2);
        };
    }

    public static Comparator<String> trackNumberComparator() {
        return (n1, n2) -> {
            var t1Num = n1 == null ? "0" : n1;
            var t2Num = n2 == null ? "0" : n2;
            if (!t1Num.equals("0") && t1Num.length() == 1) {
                t1Num = "0" +  t1Num;
            }
            if (!t2Num.equals("0") && t2Num.length() == 1) {
                t2Num = "0" +  t2Num;
            }
            return t1Num.compareTo(t2Num);
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
