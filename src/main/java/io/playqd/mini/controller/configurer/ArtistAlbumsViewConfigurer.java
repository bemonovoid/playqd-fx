package io.playqd.mini.controller.configurer;

import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.function.Supplier;

public final class ArtistAlbumsViewConfigurer extends AlbumsViewConfigurer {

    public ArtistAlbumsViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    protected void configureHeaderLeft(ItemsDescriptor itemsDescriptor, HBox headerLeft) {
        headerLeft.getChildren().add(new Label("Artist albums:"));
    }

    @Override
    public Supplier<List<MenuItem>> configureViewOptionsMenuItems(TableView<LibraryItemRow> tableView) {
        return () -> {
            var menuItems = new ArtistContextViewOptions(
                    tableView,
                    ArtistContextViewOptions.ArtistChildrenKind.ALBUMS,
                    () -> tableView.getItems().getFirst());
          return List.of(menuItems.getShowAllAlbumsMenuItem(), menuItems.getShowAllTracksMenuItem());
        };
    }
}
