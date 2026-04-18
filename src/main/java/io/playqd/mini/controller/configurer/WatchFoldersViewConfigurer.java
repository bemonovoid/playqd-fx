package io.playqd.mini.controller.configurer;

import io.playqd.mini.controller.ItemsTableColumnIds;
import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.NavigableItemsResolver;
import io.playqd.mini.controller.factories.WatchFolderImageTableCellFactory;
import io.playqd.mini.controller.factories.ImageTableCellFactory;
import io.playqd.mini.controller.item.ArtistItemRow;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.WatchFolderItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public final class WatchFoldersViewConfigurer extends DefaultItemsViewConfigurer {

    private final static Logger LOG = LoggerFactory.getLogger(WatchFoldersViewConfigurer.class);

    public WatchFoldersViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    protected Set<String> getExcludedColumns() {
        return Set.of(ItemsTableColumnIds.TAGS_COL, ItemsTableColumnIds.MISC_VALUE_COL);
    }

    @Override
    protected ImageTableCellFactory geImageTableCellFactory() {
        return new WatchFolderImageTableCellFactory();
    }

    @Override
    public void onItemsOpen(List<LibraryItemRow> items) {
        if (items.getFirst() instanceof WatchFolderItemRow watchFolderItemRow) {
            controller.showItems(NavigableItemsResolver.resolveWatchFolderItems(watchFolderItemRow));
        } else {
            LOG.warn("Unexpected item type: {}. Expected type: {}", items.getFirst().getClass(), ArtistItemRow.class);
        }
    }

    @Override
    protected void configureHeaderLeft(ItemsDescriptor itemsDescriptor, HBox headerLeft) {
        headerLeft.getChildren().add(new Label("Watch folders:"));
    }
}
