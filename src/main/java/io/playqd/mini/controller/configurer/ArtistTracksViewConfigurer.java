package io.playqd.mini.controller.configurer;

import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ArtistTracksViewConfigurer extends TracksViewConfigurer {

    public ArtistTracksViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    protected void configureHeaderLeft(ItemsDescriptor itemsDescriptor, HBox headerLeft) {
        headerLeft.getChildren().add(new Label("Artist tracks:"));
    }

    @Override
    public Supplier<List<MenuItem>> configureViewOptionsMenuItems(TableView<LibraryItemRow> tableView) {
        return () -> {
            var parentMenuItems = super.configureViewOptionsMenuItems(tableView).get();
            var menuItems = new ArtistContextViewOptions(
                    tableView,
                    ArtistContextViewOptions.ArtistChildrenKind.TRACKS,
                    () -> tableView.getItems().getFirst());
            var allItems = new ArrayList<>(parentMenuItems);
            allItems.add(new SeparatorMenuItem());
            allItems.add(menuItems.getShowAllAlbumsMenuItem());
            allItems.add(menuItems.getShowAllTracksMenuItem());
            return allItems;
        };
    }

}
