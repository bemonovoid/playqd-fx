package io.playqd.player;

import io.playqd.data.Track;

import java.util.Collections;
import java.util.List;

public class PlayerTrackListManager {

    /**
     * Replaces current list (if exists) with the new list and starts playing from the given index if requested
     */
    public static void enqueueAndPlay(TrackListRequest request) {
        Player.enqueueAndPlay(request);
    }

    public static void addNext(List<Track> tracks) {
        Player.enqueue(new TrackListRequest(trackList().size(), tracks)); //todo resolve next idx
    }

    public static void addLast(List<Track> tracks) {
        Player.enqueue(new TrackListRequest(trackList().size(), tracks));
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
}
