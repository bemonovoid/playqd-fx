package io.playqd.mini.controller.configurer;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;

import io.playqd.mini.controller.ItemsTableColumnIds;
import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.factories.ImageTableCellFactory;
import io.playqd.mini.controller.factories.MiscValueTableCellFactory;
import io.playqd.mini.controller.factories.NameTableCellFactory;
import io.playqd.mini.controller.factories.StatusTableCellFactory;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;

sealed abstract class DefaultItemsViewConfigurer implements ItemsViewConfigurer permits
        ArtistsViewConfigurer,
        AlbumsViewConfigurer,
        TracksViewConfigurer,
        PlaylistsViewConfigurer,
        CollectionsViewConfigurer,
        CollectionItemsViewConfigurer,
        WatchFoldersViewConfigurer,
        FoldersViewConfigurer {

    protected final MiniLibraryItemsViewController controller;

    DefaultItemsViewConfigurer(MiniLibraryItemsViewController controller) {
        this.controller = controller;
    }

    @Override
    public final void configureHeader(ItemsDescriptor itemsDescriptor,
                                      TableView<LibraryItemRow> tableView, HBox headerLeft, HBox headerRight) {
        headerLeft.getChildren().clear();
        configureHeaderLeft(itemsDescriptor, headerLeft);
        headerRight.getChildren().clear();
        configureHeaderRight(tableView, headerRight);
    }

    @Override
    public void configureColumns(TableView<LibraryItemRow> tableView) {
        tableView.getColumns().forEach(col -> {
            if (!getAllIncludedColumns().contains(col.getId())) {
                col.setVisible(false);
                return;
            } else {
                col.setVisible(true);
            }
            if (col.getId().equals(ItemsTableColumnIds.IMAGE_COL)) {
                @SuppressWarnings("unchecked")
                var imageCol = (TableColumn<LibraryItemRow, Long>) col;
                imageCol.setCellFactory(geImageTableCellFactory());
            } else if (col.getId().equals(ItemsTableColumnIds.NAME_COL)) {
                @SuppressWarnings("unchecked")
                var nameCol = (TableColumn<LibraryItemRow, String>) col;
                nameCol.setCellFactory(getNameTableCellFactory());
            } else if (col.getId().equals(ItemsTableColumnIds.STATUS_COL)) {
                @SuppressWarnings("unchecked")
                var statusCol = (TableColumn<LibraryItemRow, String>) col;
                if (getStatusTableCellFactory() == null) {
                    statusCol.setCellFactory(TextFieldTableCell.forTableColumn());
                } else {
                    statusCol.setCellFactory(getStatusTableCellFactory());
                }
            } else if (col.getId().equals(ItemsTableColumnIds.MISC_VALUE_COL)) {
                @SuppressWarnings("unchecked")
                var miscValueCol = (TableColumn<LibraryItemRow, String>) col;
                col.setText(getColumnNameOverrides().getOrDefault(col.getId(), ""));
                if (getMiscValueTableCellFactory() == null) {
                    miscValueCol.setCellFactory(TextFieldTableCell.forTableColumn());
                } else {
                    miscValueCol.setCellFactory(getMiscValueTableCellFactory());
                }
            }
        });
    }

    @Override
    public void configureFooter(TableView<LibraryItemRow> tableView, Label footerLabel) {
        var items = tableView.getItems();
        if (items.isEmpty()) {
            footerLabel.setText("");
        } else {
            footerLabel.setText(items.size() + " item" + (items.size() > 1 ? "s" : ""));
        }
    }

    protected Set<String> getIncludedColumns() {
        return Collections.emptySet();
    }

    protected Map<String, String> getColumnNameOverrides() {
        return Map.of();
    }

    @Override
    public Supplier<List<MenuItem>> configureViewOptionsMenuItems(TableView<LibraryItemRow> tableView) {
        return List::of;
    }

    @Override
    public List<MenuItem> configureContextMenuItems(List<LibraryItemRow> selectedItems) {
        return List.of();
    }

    protected abstract ImageTableCellFactory geImageTableCellFactory();

    protected abstract NameTableCellFactory getNameTableCellFactory();

    protected StatusTableCellFactory getStatusTableCellFactory() {
        return null;
    }

    protected MiscValueTableCellFactory getMiscValueTableCellFactory() {
        return null;
    }

    protected abstract void configureHeaderLeft(ItemsDescriptor itemsDescriptor, HBox headerLeft);

    protected void configureHeaderRight(TableView<LibraryItemRow> tableView, HBox headerRight) {

    }

    private Set<String> getAllIncludedColumns() {
        var included = new HashSet<String>();
        included.add(ItemsTableColumnIds.IMAGE_COL);
        included.add(ItemsTableColumnIds.NAME_COL);
        included.addAll(getIncludedColumns());
        return included;
    }
}
