package io.playqd.controller.library;

import io.playqd.player.PlayerTrackListManager;
import io.playqd.player.TrackListRequest;

public class MusicLibraryTracksController extends MusicLibraryAlbumsController {

    protected void initializeInternal() {
        super.initializeInternal();
        tracksView().tracksTableView().rowDoubleClickedProperty().addListener((_, _, row) -> {
            if (row != null) {
                var trackListReq = new TrackListRequest(row.index(), tracksView().tracksTableView().getItemsAsTracks());
                PlayerTrackListManager.enqueue(trackListReq);
            }
        });
    }
}
