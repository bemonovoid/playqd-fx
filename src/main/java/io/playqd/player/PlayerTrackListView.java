package io.playqd.player;

import io.playqd.data.Track;

import java.util.List;

abstract class PlayerTrackListView {

    abstract void setItems(List<Track> tracks);

    abstract int addNext(List<Track> tracks);

    abstract int addLast(List<Track> tracks);
}
