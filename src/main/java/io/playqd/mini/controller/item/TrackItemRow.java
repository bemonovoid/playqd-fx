package io.playqd.mini.controller.item;

import io.playqd.data.Track;
import javafx.beans.property.SimpleStringProperty;

public sealed class TrackItemRow extends LibraryItemRow permits AlbumTrackItemRow, QueuedTrackItemRow {

    private final Track track;

    public TrackItemRow(Track track) {
        super(new SimpleStringProperty(track.length().readable()));
        this.track = track;
    }

    @Override
    public long getId() {
        return track.id();
    }

    @Override
    public String getName() {
        return track.title();
    }

    @Override
    public String getDescription() {
        return track.artistName();
    }

    @Override
    public Track getSource() {
        return track;
    }
}
