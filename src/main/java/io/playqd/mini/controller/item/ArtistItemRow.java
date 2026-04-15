package io.playqd.mini.controller.item;

import io.playqd.data.Artist;

public final class ArtistItemRow extends LibraryItemRow {

    private final Artist artist;

    public ArtistItemRow(Artist artist) {
        this.artist = artist;
    }

    @Override
    public long getId() {
        return artist.id();
    }

    @Override
    public String getName() {
        return artist.name();
    }

    @Override
    public Artist getSource() {
        return artist;
    }
}
