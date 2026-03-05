package io.playqd.controller.library;

import io.playqd.data.Album;
import io.playqd.player.PlayRequest;
import io.playqd.player.PlayerEngine;

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
        PlayerEngine.enqueueAndPlay(new PlayRequest(musicSplitPaneController.tracksView().tracksTableView().getItems()));
    }
}
