package io.playqd.player;

import io.playqd.data.Track;

import java.util.Optional;
import java.util.function.Consumer;

public interface PlayerEventConsumerRegistry {

    void addPausedConsumer(Runnable consumer);

    void addFinishedConsumer(Runnable consumer);

    void addStoppedConsumer(Runnable consumer);

    void addPlayingConsumer(Consumer<Optional<Track>> consumer);

    void addPositionChangedConsumer(Consumer<Float> consumer);

    void addTimeChangedConsumer(Consumer<Long> consumer);
}
