package io.playqd.player;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import io.playqd.config.AppConfig;
import io.playqd.data.Track;

public final class PlayerObservableProperties {

    private final SimpleObjectProperty<PlaybackMode> playbackMode = new SimpleObjectProperty<>(PlaybackMode.DEFAULT);
    private final SimpleObjectProperty<FetchMode> fetchMode = new SimpleObjectProperty<>(FetchMode.NORMAL);
    private final SimpleObjectProperty<Track> finished = new SimpleObjectProperty<>();
    private final SimpleBooleanProperty paused = new SimpleBooleanProperty();
    private final SimpleObjectProperty<Track> playingTrack = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<Float> positionChanged = new SimpleObjectProperty<>();
    private final SimpleBooleanProperty stopped = new SimpleBooleanProperty();
    private final SimpleObjectProperty<Long> timeChanged = new SimpleObjectProperty<>();
    private final SimpleBooleanProperty queueFinished = new SimpleBooleanProperty();

    {
        setFetchMode(AppConfig.getProperties().player().state().fetchMode().get());
        setPlaybackMode(AppConfig.getProperties().player().state().playbackMode().get());
        AppConfig.getProperties().player().state().fetchMode().bind(fetchMode);
        AppConfig.getProperties().player().state().playbackMode().bind(playbackMode);
    }

    public ReadOnlyObjectProperty<FetchMode> fetchMode() {
        return fetchMode;
    }

    public ReadOnlyObjectProperty<PlaybackMode> playbackMode() {
        return playbackMode;
    }

    public ReadOnlyObjectProperty<Track> finished() {
        return finished;
    }

    public ReadOnlyBooleanProperty paused() {
        return paused;
    }

    public ReadOnlyBooleanProperty stopped() {
        return stopped;
    }

    public ReadOnlyObjectProperty<Long> timeChanged() {
        return timeChanged;
    }

    public ReadOnlyObjectProperty<Track> playingTrack() {
        return playingTrack;
    }

    public ReadOnlyObjectProperty<Float> positionChanged() {
        return positionChanged;
    }

    public ReadOnlyBooleanProperty queueFinished() {
        return queueFinished;
    }

    void setFetchMode(FetchMode fetchMode) {
        this.fetchMode.set(fetchMode);
    }

    void setPlaybackMode(PlaybackMode mode) {
        playbackMode.set(mode);
    }

    void setFinishedProperty(Track track) {
        finished.set(track);
    }

    void setPausedProperty(boolean paused) {
        this.paused.set(paused);
    }

    void setStoppedProperty(boolean stopped) {
        this.stopped.set(stopped);
    }

    void setPlayingTrackProperty(Track track) {
        playingTrack.set(track);
    }

    void setPositionChangedProperty(float position) {
        positionChanged.set(position);
    }

    void setTimeChangedProperty(long timeChanged) {
        this.timeChanged.set(timeChanged);
    }

    void setQueueFinishedProperty(boolean queueFinished) {
        this.queueFinished.set(queueFinished);
    }

}
