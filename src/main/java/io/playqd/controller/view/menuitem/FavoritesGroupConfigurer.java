package io.playqd.controller.view.menuitem;

import io.playqd.data.Track;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.MenuItem;

import java.util.List;
import java.util.stream.Stream;

public record FavoritesGroupConfigurer(MenuItemConfigurer add, MenuItemConfigurer remove) implements GroupConfigurer{

    static FavoritesGroupConfigurer build() {
        var add = new MenuItemConfigurer("Add to favorites", tracks -> tracks.stream()
                .filter(t -> t.rating().value() <= 0)
                .forEach(t -> MusicLibrary.addToFavorites(t.id())));
        var remove = new MenuItemConfigurer("Remove from favorites", tracks -> tracks.stream()
                .filter(t -> t.rating().value() > 0)
                .forEach(t -> MusicLibrary.removeFromFavorites(t.id())));
        return new FavoritesGroupConfigurer(add, remove);
    }

    @Override
    public List<MenuItem> configure(List<Track> selectedTracks) {
        add.setDisabled(selectedTracks.stream().allMatch(t -> t.rating().value() > 0));
        remove.setDisabled(selectedTracks.stream().allMatch(t -> t.rating().value() <= 0));
        return Stream.of(add, remove, new SeparatorMenuItemConfigurer())
                .filter(c -> !c.isExcluded())
                .map(c -> c.configure(selectedTracks))
                .toList();
    }
}
