package io.playqd.controller.view.menuitem;

import io.playqd.data.Track;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class TrackContextMenuConfigurer {

    protected final PlayGroupConfigurer playGroupConfigurer;
    private final FavoritesGroupConfigurer favoritesGroupConfigurer;
    protected final PlaylistGroupConfigurer playlistGroupConfigurer;

    public TrackContextMenuConfigurer() {
        this.playGroupConfigurer = PlayGroupConfigurer.build();
        this.favoritesGroupConfigurer = FavoritesGroupConfigurer.build();
        this.playlistGroupConfigurer = PlaylistGroupConfigurer.build();
    }

    public List<MenuItem> configure(List<Track> selectedTracks) {
        var playGroupItems = playGroupConfigurer.configure(selectedTracks);
        var favoriteGroupItems = favoritesGroupConfigurer.configure(selectedTracks);
        var playlistGroupItems = playlistGroupConfigurer.configure(selectedTracks);
        var allItems = new ArrayList<MenuItem>(
                playGroupItems.size() + favoriteGroupItems.size() + playlistGroupItems.size());
        allItems.addAll(playGroupItems);
        allItems.addAll(favoriteGroupItems);
        allItems.addAll(playlistGroupItems);
        return allItems;
    }
}
