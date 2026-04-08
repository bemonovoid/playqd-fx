package io.playqd.controller.library;

import io.playqd.data.Album;
import io.playqd.player.PlayerTrackListManager;
import io.playqd.player.TrackListRequest;

class AlbumsListViewCellFactoryListener {

    private final MusicLibraryViewController musicLibraryViewController;

    AlbumsListViewCellFactoryListener(MusicLibraryViewController musicLibraryViewController) {
        this.musicLibraryViewController = musicLibraryViewController;
    }

    void onAllArtistAlbumsClicked(long artistId) {
        musicLibraryViewController.tracksView().tracksTableView()
                .showTracks(() -> musicLibraryViewController.getArtistTracks(artistId));
    }

    void onAlbumDoubleClicked(Album album) {
        var trackListReq = new TrackListRequest(
                musicLibraryViewController.tracksView().tracksTableView().getItemsAsTracks());
        PlayerTrackListManager.enqueue(trackListReq);
    }
}
