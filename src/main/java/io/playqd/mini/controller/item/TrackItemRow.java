package io.playqd.mini.controller.item;

import io.playqd.data.Track;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public sealed class TrackItemRow extends LibraryItemRow
        permits ArtistTrackItemRow, AlbumTrackItemRow, QueuedTrackItemRow, PlaylistTrackItemRow {

    private final ObjectProperty<Track> trackProperty;

    public TrackItemRow(Track track) {
        super(new SimpleStringProperty(track.length().readable()));
        this.trackProperty = new SimpleObjectProperty<>(track);
    }

    @Override
    public long getId() {
        return this.trackProperty.get().id();
    }

    @Override
    public String getName() {
        return this.trackProperty.get().name();
    }

    @Override
    public String getDescription() {
        return this.trackProperty.get().artistName();
    }

    @Override
    public Track getSource() {
        return this.trackProperty.get();
    }

    public void setTrack(Track track) {
        this.trackProperty.set(track);
    }
}
