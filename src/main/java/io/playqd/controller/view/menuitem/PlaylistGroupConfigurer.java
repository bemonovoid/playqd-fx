package io.playqd.controller.view.menuitem;

import io.playqd.data.Track;
import javafx.scene.control.MenuItem;

import java.util.List;
import java.util.stream.Stream;

public record PlaylistGroupConfigurer(MenuConfigurer addToPlaylist,
                                      MenuConfigurer moveToPlaylist,
                                      MenuItemConfigurer removeFromPlaylist) implements GroupConfigurer {
    @Override
    public List<MenuItem> configure(List<Track> selectedTracks) {

        return Stream.of(addToPlaylist, moveToPlaylist, removeFromPlaylist)
                .filter(c -> !c.isExcluded())
                .map(c -> c.configure(selectedTracks))
                .toList();
    }
}
