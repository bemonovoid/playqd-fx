package io.playqd.mini.controller.configurer;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;

public final class AlbumTracksViewConfigurer extends TracksViewConfigurer {

    public AlbumTracksViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    protected void configureHeaderLeft(ItemsDescriptor itemsDescriptor, HBox headerLeft) {
        headerLeft.getChildren().add(new Label("Album tracks:"));
    }

    @Override
    public void onOpen(TableView<LibraryItemRow> tableView) {
        enqueueAll(tableView);
    }
}
