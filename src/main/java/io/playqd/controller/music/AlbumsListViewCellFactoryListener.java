package io.playqd.controller.music;

import io.playqd.data.Album;
import io.playqd.player.PlayRequest;
import io.playqd.player.PlayerEngine;
import javafx.collections.FXCollections;

public class AlbumsListViewCellFactoryListener {

    private final MusicSplitPaneController musicSplitPaneController;

    public AlbumsListViewCellFactoryListener(MusicSplitPaneController musicSplitPaneController) {
        this.musicSplitPaneController = musicSplitPaneController;
    }

    public void onAllArtistAlbumsClicked(String artistId) {
        musicSplitPaneController.getTracksContainer().getTracksTableView().setItems(
                FXCollections.observableArrayList(musicSplitPaneController.getArtistTracks(artistId)));
    }

    public void onAlbumDoubleClicked(Album album) {
        PlayerEngine.enqueueAndPlay(new PlayRequest(musicSplitPaneController.getTracksContainer().getTracksTableView().getItems()));
    }
}
