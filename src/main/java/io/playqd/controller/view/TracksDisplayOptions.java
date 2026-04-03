package io.playqd.controller.view;

public final class TracksDisplayOptions {

    private boolean disableCueParentTracks;

    public TracksDisplayOptions disableCueParentTracks(boolean value) {
        this.disableCueParentTracks = value;
        return this;
    }
}
