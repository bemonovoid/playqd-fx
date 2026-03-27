package io.playqd.event;

import io.playqd.data.Track;
import javafx.event.EventType;

import java.util.List;

public final class TracksUpdatedEvent extends PlayqdEvent {

    public static final EventType<TracksUpdatedEvent> TRACKS_UPDATED_EVENT =
            new EventType<>("TRACKS_UPDATED_EVENT");

    private final TrackUpdateType type;
    private final List<Track> tracks;

    public TracksUpdatedEvent(TrackUpdateType type, List<Track> tracks) {
        super(TRACKS_UPDATED_EVENT);
        this.type = type;
        this.tracks = tracks;
    }

    public TrackUpdateType type() {
        return type;
    }

    public List<Track> tracks() {
        return tracks;
    }
}
