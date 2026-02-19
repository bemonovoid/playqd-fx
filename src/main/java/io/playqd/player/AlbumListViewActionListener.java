package io.playqd.player;

import io.playqd.data.Album;

public interface AlbumListViewActionListener {

    default void onAllArtistAlbumsClicked(String artistId) {

    }

    default void onAlbumDoubleClicked(Album album) {

    }
}
