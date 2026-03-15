package io.playqd.controller.library;

import io.playqd.player.PlayRequest;
import io.playqd.player.Player;

public class MusicLibraryTracksController extends MusicLibraryAlbumsController {

    protected void initializeInternal() {
        super.initializeInternal();
        tracksView().tracksTableView().rowDoubleClickedProperty().addListener((_, _, row) -> {
            if (row != null) {
                Player.enqueueAndPlay(new PlayRequest(tracksView().tracksTableView().getItemsAsTracks(), row.index()));
            }
        });
    }
}
