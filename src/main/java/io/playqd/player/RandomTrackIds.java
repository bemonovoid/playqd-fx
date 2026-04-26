package io.playqd.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import io.playqd.data.Tuple;

public final class RandomTrackIds {

    private final LinkedHashSet<Tuple<Long, Integer>> randomTracks = new LinkedHashSet<>();

    public RandomTrackIds(List<Long> tracks) {
        var shuffled = new ArrayList<Tuple<Long, Integer>>(tracks.size());
        for (int i = 0; i < tracks.size(); i++) {
            shuffled.add(Tuple.from(tracks.get(i), i));
        }
        Collections.shuffle(shuffled);
        randomTracks.addAll(shuffled);
    }

    public boolean isEmpty() {
        return randomTracks.isEmpty();
    }

    public boolean hasNext() {
        return !randomTracks.isEmpty();
    }

    public Long poll(long trackId) {
        if (isEmpty()) {
            return null;
        }
        return randomTracks.removeIf(t -> t.left().equals(trackId)) ? trackId : null;
    }

    public Long next() {
        if (isEmpty()) {
            return null;
        }
        return randomTracks.removeLast().left();
    }
}
