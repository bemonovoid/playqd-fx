package io.playqd.controller.view.menuitem;

import io.playqd.data.Track;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.util.List;

public final class SeparatorMenuItemConfigurer extends MenuItemConfigurer {

    public SeparatorMenuItemConfigurer() {
        super("");
    }

    @Override
    public MenuItem configure(List<Track> selectedTracks) {
        return new SeparatorMenuItem();
    }
}
