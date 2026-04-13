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
    public String getDescription() {
        var albumsCount = artist.albumsCount();
        var tracksCount = artist.tracksCount();
        return String.format("%s album%s, %s track%s",
                albumsCount,
                albumsCount > 1 ? "s" : "",
                tracksCount,
                tracksCount > 1 ? "s" : "");
    }

    @Override
    public Artist getSource() {
        return artist;
    }
}
