package io.playqd.mini.controller.configurer;

import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

import java.util.List;

public sealed interface ItemsViewConfigurer permits DefaultItemsViewConfigurer {

    void configureColumns(TableView<LibraryItemRow> tableView);

    void configureHeader(ItemsDescriptor itemsDescriptor,
                         TableView<LibraryItemRow> tableView,
                         HBox headerLeft,
                         HBox headerRight);

    void configureFooter(TableView<LibraryItemRow> tableView, Label footerLabel);

    void onRowOpened(LibraryItemRow item);

    List<MenuItem> configureContextMenuItems(List<LibraryItemRow> selectedItems);

}
