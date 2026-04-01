package io.playqd.service;

public class TrackFilters {

    private boolean excludeCueParent = false;
    private boolean excludeCueChildren = false;

    public TrackFilters excludeCueParent(boolean excludeCueParent) {
        this.excludeCueParent = excludeCueParent;
        return this;
    }

    public TrackFilters excludeCueChildren(boolean excludeCueChildren) {
        this.excludeCueChildren = excludeCueChildren;
        return this;
    }
}
