package io.playqd.player;


import io.playqd.data.Track;

public final class QueuedTrack {

    private final Track track;
    private boolean visited;

    public QueuedTrack(Track track) {
        this(track, false);
    }

    public QueuedTrack(Track track, boolean visited) {
        this.track = track;
        this.visited = visited;
    }

    public Track track() {
        return track;
    }

    public boolean visited() {
        return visited;
    }

    public void setVisited(boolean value) {
        visited = value;
    }

}
