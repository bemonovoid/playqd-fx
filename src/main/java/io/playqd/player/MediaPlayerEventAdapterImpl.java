package io.playqd.player;

import io.playqd.data.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

class MediaPlayerEventAdapterImpl extends MediaPlayerEventAdapter implements PlayerEventConsumerRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(MediaPlayerEventAdapterImpl.class);

    private final Collection<Consumer<Float>> positionChangedConsumers = new ArrayList<>();
    private final Collection<Consumer<Long>> timeChangedConsumers = new ArrayList<>();
    private final Collection<Consumer<Optional<Track>>> playingConsumers = new ArrayList<>();
    private final Collection<Runnable> pausedConsumers = new ArrayList<>();
    private final Collection<Runnable> stoppedConsumers = new ArrayList<>();
    private final Collection<Runnable> finishedConsumers = new ArrayList<>();
    private final PlayingQueue playingQueue;

    MediaPlayerEventAdapterImpl(PlayingQueue playingQueue) {
        this.playingQueue = playingQueue;
    }

    @Override
    public void paused(MediaPlayer mediaPlayer) {
        if (!pausedConsumers.isEmpty()) {
            pausedConsumers.forEach(Runnable::run);
        }
    }

    @Override
    public void playing(MediaPlayer mediaPlayer) {
        if (!playingConsumers.isEmpty()) {
            playingConsumers.forEach(consumer -> consumer.accept(playingQueue.current()));
        }
    }

    @Override
    public void stopped(MediaPlayer mediaPlayer) {
        if (!stoppedConsumers.isEmpty()) {
            stoppedConsumers.forEach(Runnable::run);
        }
    }

    @Override
    public void finished(MediaPlayer mediaPlayer) {
        if (!finishedConsumers.isEmpty()) {
            finishedConsumers.forEach(Runnable::run);
        }
    }

    @Override
    public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
        if (!positionChangedConsumers.isEmpty()) {
            positionChangedConsumers.forEach(consumer -> consumer.accept(newPosition));
        }
    }

    @Override
    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
        if (!timeChangedConsumers.isEmpty()) {
            timeChangedConsumers.forEach(consumer -> consumer.accept(newTime));
        }
    }

    @Override
    public void addPausedConsumer(Runnable consumer) {
        if (consumer != null) {
            this.pausedConsumers.add(consumer);
        }
    }

    @Override
    public void addFinishedConsumer(Runnable consumer) {
        if (consumer != null) {
            this.finishedConsumers.add(consumer);
        }
    }

    @Override
    public void addStoppedConsumer(Runnable consumer) {
        if (consumer != null) {
            this.stoppedConsumers.add(consumer);
        }
    }

    @Override
    public void addPlayingConsumer(Consumer<Optional<Track>> consumer) {
        if (consumer != null) {
            this.playingConsumers.add(consumer);
        }
    }

    @Override
    public void addPositionChangedConsumer(Consumer<Float> consumer) {
        if (consumer != null) {
            this.positionChangedConsumers.add(consumer);
        }
    }

    @Override
    public void addTimeChangedConsumer(Consumer<Long> consumer) {
        if (consumer != null) {
            this.timeChangedConsumers.add(consumer);
        }
    }
}
