package io.playqd.controller.view.menuitem;

import io.playqd.data.Track;
import javafx.scene.control.MenuItem;

import java.util.List;
import java.util.stream.Stream;

public record PlayGroupConfigurer(MenuItemConfigurer playNow,
                                  MenuItemConfigurer queueNext,
                                  MenuItemConfigurer queueLast) implements GroupConfigurer {

    @Override
    public List<MenuItem> configure(List<Track> selectedTracks) {
        return Stream.of(playNow, queueNext, queueLast, new SeparatorMenuItemConfigurer())
                .filter(c -> !c.isExcluded())
                .map(c -> c.configure(selectedTracks))
                .toList();
    }
}
