package io.playqd.mini.controller.configurer;

import io.playqd.controller.collections.CollectionDialog;
import io.playqd.data.MediaCollection;
import io.playqd.mini.controller.ItemsTableColumnIds;
import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.NavigableItemsResolver;
import io.playqd.mini.controller.factories.*;
import io.playqd.mini.controller.item.CollectionItemRow;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.PlaylistItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import io.playqd.mini.custom.ConfirmDeleteRowItemsDialog;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class CollectionsViewConfigurer extends DefaultItemsViewConfigurer {

    private final static Logger LOG = LoggerFactory.getLogger(CollectionsViewConfigurer.class);

    public CollectionsViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    protected ImageTableCellFactory geImageTableCellFactory() {
        return new CollectionImageTableCellFactory();
    }

    @Override
    protected MiscValueTableCellFactory getMiscValueTableCellFactory() {
        return new CollectionItemsCountTableCellFactory(libraryItemRow -> {
            if (libraryItemRow instanceof CollectionItemRow collectionItemRow) {
                return collectionItemRow.getSource().items().size();
            }
            LOG.trace("Unexpected row type: {}", libraryItemRow.getClass());
            return -1;
        });
    }

    @Override
    protected void configureHeaderLeft(ItemsDescriptor itemsDescriptor, HBox headerLeft) {
        headerLeft.getChildren().add(new Label("Collections:"));
    }

    @Override
    public Supplier<List<MenuItem>> configureViewOptionsMenuItems(TableView<LibraryItemRow> tableView) {
        return () -> {
            var newPlaylistMenuItem = new MenuItem("New Collection", new FontIcon("fas-plus"));
            newPlaylistMenuItem.setOnAction(_ -> {
                var dialog = new CollectionDialog();
                dialog.showAndWait().ifPresent(name -> {
                    if (!name.trim().isEmpty()) {
                        var collectionItemRow = new CollectionItemRow(MusicLibrary.createCollection(name));
                        tableView.getItems().add(collectionItemRow);
                        tableView.refresh();
                        tableView.getSelectionModel().select(collectionItemRow);
                    }
                });
            });
            var deleteEmptyMenuItems = new MenuItem("Delete empty collections", new FontIcon("fas-times"));
            deleteEmptyMenuItems.setOnAction(_ -> {
                var emptyCollections = tableView.getItems().stream()
                        .map(i -> (CollectionItemRow) i)
                        .filter(c -> c.getSource().items().isEmpty())
                        .toList();
                var confirmed = ConfirmDeleteRowItemsDialog.confirmDelete(emptyCollections);
                if (confirmed) {
                    emptyCollections.forEach(i -> MusicLibrary.deleteCollection(i.getId()));
                    controller.refreshLastState();
                }
            });
            return List.of(newPlaylistMenuItem, deleteEmptyMenuItems);
        };
    }

    @Override
    public void onItemsOpen(List<LibraryItemRow> items) {
        if (items.getFirst() instanceof CollectionItemRow collectionItemRow) {
            controller.showItems(NavigableItemsResolver.resolveCollectionItems(collectionItemRow));
        } else {
            LOG.warn("Unexpected item type: {}. Expected type: {}", items.getFirst().getClass(), PlaylistItemRow.class);
        }
    }

    @Override
    public Optional<Consumer<List<LibraryItemRow>>> onItemsDelete() {
        return Optional.of(libraryItemRows -> libraryItemRows.stream()
                .filter(item -> item instanceof CollectionItemRow)
                .forEach(collectionItemRow -> MusicLibrary.deleteCollection(collectionItemRow.getId())));
    }

    @Override
    protected Set<String> getExcludedColumns() {
        return Set.of(ItemsTableColumnIds.DESCRIPTION_COL, ItemsTableColumnIds.TAGS_COL);
    }
}
