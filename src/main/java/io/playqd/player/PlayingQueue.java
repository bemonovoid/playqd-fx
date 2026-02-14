package io.playqd.player;

import io.playqd.data.Track;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlayingQueue {

    private static final Logger LOG = LoggerFactory.getLogger(PlayingQueue.class);

    private final AtomicInteger currentPosition = new AtomicInteger();
    private final ObservableList<QueuedTrack> queue = FXCollections.observableArrayList();
    private FetchMode fetchMode = FetchMode.ORDINAL;
    private LoopMode loopMode = LoopMode.NONE;

    public void clear() {
        queue.clear();
        currentPosition.set(0);
    }

    public void enqueue(List<Track> tracks) {
        var queuedTracks = tracks.stream().map(QueuedTrack::new).toList();
        queue.addAll(queuedTracks);
    }

    public Optional<Track> get(int position) {
        return Optional.ofNullable(queue.get(position)).map(QueuedTrack::track);
    }

    public Optional<Track> current() {
        return currentQueued().map(QueuedTrack::track);
    }

    private Optional<QueuedTrack> currentQueued() {
        if (isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(queue.get(currentPosition.get()));
    }

    public boolean hasNext() {
        if (isEmpty()) {
            return false;
        }
        if (LoopMode.NONE != loopMode) {
            return true;
        }
        if (FetchMode.ORDINAL == fetchMode && currentPosition.get() == queue.size() - 1) {
            return false;
        }
        return queue.stream().anyMatch(t -> !t.visited());
    }

    public Optional<Track> next() {
        if (isEmpty() || !hasNext()) {
            return Optional.empty();
        }

        if (LoopMode.SINGLE == loopMode) {
            return current();
        }

        var next = Optional.<QueuedTrack>empty();

        if (FetchMode.RANDOM == fetchMode) {
            next = nextRandom();
        } else if (queue.size() - 1 == currentPosition.get() && LoopMode.ALL == loopMode) {
            currentPosition.set(0);
            next = Optional.of(queue.getFirst());
        } else {
            currentPosition.incrementAndGet();
            next = currentQueued();
        }

        next.ifPresent(queuedTrack -> queuedTrack.setVisited(true));

        return next.map(QueuedTrack::track);
    }

    public void setFetchMode(FetchMode fetchMode) {
        resetVisited();
        this.fetchMode = fetchMode;
    }

    public void setLoopMode(LoopMode loopMode) {
        this.loopMode = loopMode;
    }

    public void setStartingPosition(int position) {
        this.currentPosition.set(position);
    }

    public void setVisited(int position) {
        queue.get(position).setVisited(true);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void resetVisited() {
        queue.forEach(queuedTrack -> queuedTrack.setVisited(false));
    }

    ObservableList<QueuedTrack> queuedTracks() {
        return queue;
    }

    private Optional<QueuedTrack> nextRandom() {
        var notVisited = queue.stream().filter(t -> !t.visited()).collect(Collectors.toCollection(ArrayList::new));

        if (notVisited.isEmpty()) {
            return Optional.empty();
        }

        Collections.shuffle(notVisited);

        var nextRandom = notVisited.getFirst();

        IntStream.range(0, queue.size())
                .filter(idx -> queue.get(idx).track().id() == nextRandom.track().id())
                .findFirst()
                .ifPresent(currentPosition::set);

        return Optional.of(nextRandom);
    }

}
