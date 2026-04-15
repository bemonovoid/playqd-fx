package io.playqd.mini.controller.configurer;

import io.playqd.mini.controller.ItemsTableColumnIds;
import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.factories.DescriptionTableCellFactory;
import io.playqd.mini.controller.factories.ImageTableCellFactory;
import io.playqd.mini.controller.factories.MiscValueTableCellFactory;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;

import java.util.Collections;
import java.util.List;
import java.util.Set;

sealed abstract class DefaultItemsViewConfigurer implements ItemsViewConfigurer
        permits ArtistsViewConfigurer, AlbumsViewConfigurer, TracksViewConfigurer, PlaylistsViewConfigurer {

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
            if (getExcludedColumns().contains(col.getId())) {
                col.prefWidthProperty().unbind();
                col.maxWidthProperty().unbind();
                col.setVisible(false);
                rebindNameColumnWidths(tableView);
                return;
            } else {
                col.setVisible(true);
            }
            if (col.getId().equals(ItemsTableColumnIds.IMAGE_COL)) {
                @SuppressWarnings("unchecked")
                var imageCol = (TableColumn<LibraryItemRow, Long>) col;
                imageCol.setCellFactory(geImageTableCellFactory());
            } else if (col.getId().equals(ItemsTableColumnIds.DESCRIPTION_COL)) {
                @SuppressWarnings("unchecked")
                var descriptionCol = (TableColumn<LibraryItemRow, String>) col;
                if (getDescriptionTableCellFactory() == null) {
                    descriptionCol.setCellFactory(TextFieldTableCell.forTableColumn());
                } else {
                    descriptionCol.setCellFactory(getDescriptionTableCellFactory());
                }
            } else if (col.getId().equals(ItemsTableColumnIds.MISC_VALUE_COL)) {
                @SuppressWarnings("unchecked")
                var miscValueCol = (TableColumn<LibraryItemRow, String>) col;
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

    protected Set<String> getExcludedColumns() {
        return Collections.emptySet();
    }

    @Override
    public List<MenuItem> configureContextMenuItems(List<LibraryItemRow> selectedItems) {
        return List.of();
    }

    private void rebindNameColumnWidths(TableView<LibraryItemRow> tableView) {
        tableView.getColumns().stream()
                .filter(c -> c.getId().equals(ItemsTableColumnIds.NAME_COL))
                .findFirst()
                .ifPresent(col -> {
                    col.prefWidthProperty().unbind();
                    col.maxWidthProperty().unbind();
                    col.setPrefWidth(tableView.widthProperty().multiply(0.7).getValue());
                    col.setMaxWidth(tableView.widthProperty().multiply(0.7).getValue());
                    col.prefWidthProperty().bind(tableView.widthProperty().multiply(0.7));
                    col.maxWidthProperty().bind(tableView.widthProperty().multiply(0.7));
                });
    }

    protected abstract ImageTableCellFactory geImageTableCellFactory();

    protected abstract DescriptionTableCellFactory getDescriptionTableCellFactory();

    protected abstract MiscValueTableCellFactory getMiscValueTableCellFactory();

    protected abstract void configureHeaderLeft(ItemsDescriptor itemsDescriptor, HBox headerLeft);

    protected abstract void configureHeaderRight(TableView<LibraryItemRow> tableView, HBox headerRight);
}
