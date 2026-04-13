package io.playqd.mini.controller.configurer;

import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.item.LibraryItemRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

sealed abstract class DefaultItemsViewConfigurer implements ItemsViewConfigurer
        permits ArtistsItemsViewConfigurer, AlbumsItemsViewConfigurer, TracksItemsViewConfigurer {

    protected final MiniLibraryItemsViewController controller;

    DefaultItemsViewConfigurer(MiniLibraryItemsViewController controller) {
        this.controller = controller;
    }

    @Override
    public final void configureHeader(TableView<LibraryItemRow> tableView, HBox headerLeft, HBox headerRight) {
        headerLeft.getChildren().clear();
        configureHeaderLeft(tableView, headerLeft);
        headerRight.getChildren().clear();
        configureHeaderRight(tableView, headerRight);
    }

    protected abstract void configureHeaderLeft(TableView<LibraryItemRow> tableView, HBox headerLeft);

    protected abstract void configureHeaderRight(TableView<LibraryItemRow> tableView, HBox headerRight);
}
