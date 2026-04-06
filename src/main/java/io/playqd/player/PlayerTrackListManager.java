package io.playqd.player;

import io.playqd.data.Track;

import java.util.Collections;
import java.util.List;

public class PlayerTrackListManager {

    private static PlayerTrackListView PLAYER_TRACK_LIST_VIEW;

    /**
     * Replaces current list (if exists) with the new list and starts playing from the given index if requested
     */
    public static void enqueue(TrackListRequest request) {
        PLAYER_TRACK_LIST_VIEW.setItems(request.tracks());
        Player.enqueueAndPlay(request);
    }

    public static void addNext(List<Track> tracks) {
        var insertIdx = PLAYER_TRACK_LIST_VIEW.addNext(tracks);
        Player.enqueue(new TrackListRequest(insertIdx, tracks));
    }

    public static void addLast(List<Track> tracks) {
        var insertIdx = PLAYER_TRACK_LIST_VIEW.addLast(tracks);
        Player.enqueue(new TrackListRequest(insertIdx, tracks));
    }

    public static List<Track> trackList() {
        var trackRefs = Player.getPlayerListTrackRefs();
        if (trackRefs == null) {
            return Collections.emptyList();
        }
        return trackRefs.stream().map(TrackRef::track).toList();
    }

    // Indices must be in descending order
    static void remove(List<Integer> indices) {
        Player.remove(indices);
    }

    static void clear() {
        Player.clearMediaList();
    }

    static void setPlayerTrackListView(PlayerTrackListView playerTrackListView) {
        if (PLAYER_TRACK_LIST_VIEW == null) {
            PLAYER_TRACK_LIST_VIEW = playerTrackListView;
        }
    }
}
