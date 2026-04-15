package io.playqd.mini.controller.item;

import io.playqd.data.Playlist;

public final class PlaylistItemRow extends LibraryItemRow {

    private final Playlist playlist;

    public PlaylistItemRow(Playlist playlist) {
        this.playlist = playlist;
    }

    @Override
    public long getId() {
        return playlist.id();
    }

    @Override
    public String getName() {
        return playlist.name();
    }

    @Override
    public String getDescription() {
        return playlist.tracks().size() + " tracks";
    }

    @Override
    public Playlist getSource() {
        return playlist;
    }
}
