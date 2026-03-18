package io.playqd.controller.library;

import io.playqd.player.PlayerTrackListManager;
import io.playqd.player.TrackListRequest;
import io.playqd.player.Player;

public class MusicLibraryTracksController extends MusicLibraryAlbumsController {

    protected void initializeInternal() {
        super.initializeInternal();
        tracksView().tracksTableView().rowDoubleClickedProperty().addListener((_, _, row) -> {
            if (row != null) {
                var trackListReq = new TrackListRequest(tracksView().tracksTableView().getItemsAsTracks(), row.index());
                PlayerTrackListManager.enqueue(trackListReq);
            }
        });
    }
}
