package io.playqd.player;

import io.playqd.data.Track;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlayerTrackListManager {

    private static PlayerTrackListView PLAYER_TRACK_LIST_VIEW;
    private static final ObservableList<Track> TRACK_LIST = FXCollections.observableArrayList();

    /**
     * Replaces current list (if exists) with the new list and starts playing from the given index
     */
    public static void enqueue(TrackListRequest request) {
        PLAYER_TRACK_LIST_VIEW.setItems(request.tracks());
        Player.enqueueAndPlay(request);
    }

    public static void removeFromList() {

    }

    public static void clearList() {

    }

    static void setPlayerTrackListView(PlayerTrackListView playerTrackListView) {
        if (PLAYER_TRACK_LIST_VIEW == null) {
            PLAYER_TRACK_LIST_VIEW = playerTrackListView;
        }
    }
}
