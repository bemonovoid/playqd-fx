package io.playqd.mini.controller.item;

import io.playqd.data.Album;
import javafx.beans.property.SimpleStringProperty;

public sealed class AlbumItemRow extends LibraryItemRow permits ArtistAlbumItemRow {

    private final Album album;

    public AlbumItemRow(Album album) {
        super(new SimpleStringProperty("" + album.tracksCount()));
        this.album = album;
    }

    @Override
    public long getId() {
        return album.id();
    }

    @Override
    public String getName() {
        return album.name();
    }

    @Override
    public String getDescription() {
        var tracksCount = album.tracksCount();
        return String.format("%s, %s", album.releaseDate(), album.genre());
    }

    @Override
    public Album getSource() {
        return album;
    }
}
