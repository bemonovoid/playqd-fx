package io.playqd.player;

import io.playqd.data.Album;

public interface AlbumGridViewActionListener {

    default void onAlbumImageClicked(Album album) {

    }

    default void onAlbumImageDoubleClicked(Album album) {

    }
}
