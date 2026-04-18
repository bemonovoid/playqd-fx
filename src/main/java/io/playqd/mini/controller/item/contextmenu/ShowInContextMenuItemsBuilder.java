package io.playqd.mini.controller.item.contextmenu;

import io.playqd.data.Track;
import io.playqd.data.WatchFolderItem;
import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.NavigableItemsResolver;
import io.playqd.mini.controller.item.FolderItemRow;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.TrackItemRow;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class ShowInContextMenuItemsBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(ShowInContextMenuItemsBuilder.class);

    private final MiniLibraryItemsViewController controller;

    private MenuItem showInFolderMenuItem;

    private ShowInContextMenuItemsBuilder(MiniLibraryItemsViewController controller) {
        this.controller = controller;
    }

    public static List<MenuItem> buildDefault(MiniLibraryItemsViewController controller, List<LibraryItemRow> items) {
        return new ShowInContextMenuItemsBuilder(controller)
                .inFolder(items)
                .inPlaylist(items)
                .inCollection(items)
                .build();
    }

    public ShowInContextMenuItemsBuilder inFolder(List<LibraryItemRow> items) {
        var canShowInFolder = items.size() == 1 && (items.getFirst() instanceof TrackItemRow);
        if (!canShowInFolder) {
            LOG.error("Can't show in folder. " +
                    "Only one selected item is allowed and selected item type must be TrackItemRow");
        }
        this.showInFolderMenuItem = new MenuItem("Folder");
        showInFolderMenuItem.setGraphic(new FontIcon("far-folder"));
        this.showInFolderMenuItem.setOnAction(_ -> {
            var track = (Track) items.getFirst().getSource();
            var trackLocation = Paths.get(track.fileAttributes().location());
            var parentPath = trackLocation.getParent().toString();
            var wfi = MusicLibrary.getWatchFolderItemByLocation(parentPath);
            var navItems = NavigableItemsResolver.resolveWatchFolderItems(new FolderItemRow(wfi));
            controller.showItems(navItems, libraryItemRow -> {
                if (libraryItemRow.getSource() instanceof WatchFolderItem tableWfi) {
                    return trackLocation.equals(tableWfi.path());
                }
                return false;
            });
        });
        return this;
    }

    public ShowInContextMenuItemsBuilder inPlaylist(List<LibraryItemRow> items) {
        return this;
    }

    public ShowInContextMenuItemsBuilder inCollection(List<LibraryItemRow> items) {
        return this;
    }

    public List<MenuItem> build() {
        var items = new ArrayList<MenuItem>();
        if (showInFolderMenuItem != null) {
            items.add(showInFolderMenuItem);
        }
        var showInMenu = new Menu("Show in");
        showInMenu.setGraphic(new FontIcon("fas-external-link-alt"));
        showInMenu.getItems().addAll(items);
        return List.of(showInMenu);
    }
}
