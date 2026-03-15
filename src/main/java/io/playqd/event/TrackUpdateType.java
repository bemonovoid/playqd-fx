package io.playqd.event;

public enum TrackUpdateType {

    LIKED,

    UNLIKED,

    PLAY_COUNT_INCR;

    public boolean isLikeOrUnlike() {
        return LIKED == this || UNLIKED == this;
    }
}
