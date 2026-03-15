package io.playqd.controller.view;

import io.playqd.controller.view.menuitem.TrackContextMenuConfigurer;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackRowContextMenu extends ContextMenu {

    private static final Logger LOG = LoggerFactory.getLogger(TrackRowContextMenu.class);

    private final TrackContextMenuConfigurer trackContextMenuConfigurer;

    public TrackRowContextMenu(TrackContextMenuConfigurer trackContextMenuConfigurer) {
        this.trackContextMenuConfigurer = trackContextMenuConfigurer;
        LOG.info("Initialized with {}", trackContextMenuConfigurer.getClass().getSimpleName());
    }

    public void show(TableRow<TrackModel> row, double anchorX, double anchorY) {
        if (this.trackContextMenuConfigurer == null) {
            LOG.warn("'trackContextMenuConfigurer' is null. Context menu will not be shown.");
            return;
        }
        if (row.getItem() == null) {
            LOG.warn("Table row's item (track) is null. Context menu will not be shown.");
            return;
        }
        var tracks = ((TracksTableView) row.getTableView()).getSelectedTracks();
        var menuItems = trackContextMenuConfigurer.configure(tracks);
        getItems().addAll(menuItems);
        LOG.info("Showing {} menu items configured with {}",
                menuItems.size(), this.trackContextMenuConfigurer.getClass().getSimpleName());
        show(row.getScene().getWindow(), anchorX, anchorY);
    }

}
