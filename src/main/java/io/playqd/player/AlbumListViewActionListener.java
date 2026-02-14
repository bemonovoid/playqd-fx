package io.playqd.player;

import io.playqd.data.Track;

public interface AlbumListViewActionListener {

    default void onAlbumDoubleClicked(Track.Album album) {

    }
}
