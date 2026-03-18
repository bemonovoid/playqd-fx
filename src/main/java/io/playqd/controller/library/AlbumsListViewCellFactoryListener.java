package io.playqd.controller.library;

import io.playqd.data.Album;
import io.playqd.player.PlayerTrackListManager;
import io.playqd.player.TrackListRequest;

public class AlbumsListViewCellFactoryListener {

    private final MusicSplitPaneController musicSplitPaneController;

    public AlbumsListViewCellFactoryListener(MusicSplitPaneController musicSplitPaneController) {
        this.musicSplitPaneController = musicSplitPaneController;
    }

    public void onAllArtistAlbumsClicked(long artistId) {
        musicSplitPaneController.tracksView().tracksTableView()
                .showTracks(() -> musicSplitPaneController.getArtistTracks(artistId));
    }

    public void onAlbumDoubleClicked(Album album) {
        var trackListReq = new TrackListRequest(
                musicSplitPaneController.tracksView().tracksTableView().getItemsAsTracks());
        PlayerTrackListManager.enqueue(trackListReq);
    }
}
