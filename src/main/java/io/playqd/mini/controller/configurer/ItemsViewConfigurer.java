package io.playqd.mini.controller.configurer;

import io.playqd.mini.controller.item.LibraryItemRow;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

public sealed interface ItemsViewConfigurer permits DefaultItemsViewConfigurer {

    void configureColumns(TableView<LibraryItemRow> tableView);

    void configureHeader(TableView<LibraryItemRow> tableView, HBox headerLeft, HBox headerRight);

    void configureFooter(TableView<LibraryItemRow> tableView, Label footerLabel);

    void onItemMouseDoubleClicked(LibraryItemRow item);

}
