package io.playqd.controller.view.menuitem;

import io.playqd.controller.trackexplorer.TracksExplorerViewController;
import io.playqd.data.Track;
import javafx.scene.control.MenuItem;

import java.util.List;

public final class TracksExplorerTrackContextMenuConfigurer extends TrackContextMenuConfigurer {

    private final TracksExplorerViewController tracksExplorerViewController;

    public TracksExplorerTrackContextMenuConfigurer(TracksExplorerViewController tracksExplorerViewController) {
        this.tracksExplorerViewController = tracksExplorerViewController;
    }

    @Override
    public List<MenuItem> configure(List<Track> selectedTracks) {
        return super.configure(selectedTracks);
    }
}
