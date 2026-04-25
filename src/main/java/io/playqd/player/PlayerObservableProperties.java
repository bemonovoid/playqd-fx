package io.playqd.player;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import io.playqd.data.Track;

public final class PlayerObservableProperties {

    private final SimpleObjectProperty<Track> FINISHED_PROPERTY = new SimpleObjectProperty<>();
    private final SimpleBooleanProperty PAUSED_PROPERTY = new SimpleBooleanProperty();
    private final SimpleObjectProperty<Track> PLAYING_TRACK_PROPERTY = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<Float> POSITION_CHANGED_PROPERTY = new SimpleObjectProperty<>();
    private final SimpleBooleanProperty STOPPED_PROPERTY = new SimpleBooleanProperty();
    private final SimpleObjectProperty<Long> TIME_CHANGED_PROPERTY = new SimpleObjectProperty<>();

    public ReadOnlyObjectProperty<Track> finished() {
        return FINISHED_PROPERTY;
    }

    public ReadOnlyBooleanProperty paused() {
        return PAUSED_PROPERTY;
    }

    public ReadOnlyBooleanProperty stopped() {
        return STOPPED_PROPERTY;
    }

    public ReadOnlyObjectProperty<Long> timeChanged() {
        return TIME_CHANGED_PROPERTY;
    }

    public ReadOnlyObjectProperty<Track> playingTrack() {
        return PLAYING_TRACK_PROPERTY;
    }

    public ReadOnlyObjectProperty<Float> positionChanged() {
        return POSITION_CHANGED_PROPERTY;
    }

    void setFinishedProperty(Track track) {
        FINISHED_PROPERTY.set(track);
    }

    void setPausedProperty(boolean paused) {
        PAUSED_PROPERTY.set(paused);
    }

    void setStoppedProperty(boolean stopped) {
        STOPPED_PROPERTY.set(stopped);
    }

    void setPlayingTrackProperty(Track track) {
        PLAYING_TRACK_PROPERTY.set(track);
    }

    void setPositionChangedProperty(float position) {
        POSITION_CHANGED_PROPERTY.set(position);
    }

    void setTimeChangedProperty(long timeChanged) {
        TIME_CHANGED_PROPERTY.set(timeChanged);
    }

}
