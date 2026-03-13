package io.playqd.controller.view.menuitem;

import io.playqd.data.Track;
import javafx.scene.control.MenuItem;

import java.util.List;

public interface GroupConfigurer {

    List<MenuItem> configure(List<Track> selectedTracks);
}
