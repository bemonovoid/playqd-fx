package io.playqd.mini.controller.configurer;

import io.playqd.data.MediaCollectionItem;
import io.playqd.data.MediaItemType;
import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.NavigableItemsResolver;
import io.playqd.mini.controller.factories.CollectionItemImageTableCellFactory;
import io.playqd.mini.controller.factories.ImageTableCellFactory;
import io.playqd.mini.controller.factories.NameTableCellFactory;
import io.playqd.mini.controller.item.CollectionChildItemRow;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.PlaylistItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import io.playqd.player.PlayerTrackListManager;
import io.playqd.player.TrackListRequest;
import io.playqd.service.MusicLibrary;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class CollectionItemsViewConfigurer extends DefaultItemsViewConfigurer {

    private final static Logger LOG = LoggerFactory.getLogger(CollectionItemsViewConfigurer.class);

    public CollectionItemsViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    protected ImageTableCellFactory geImageTableCellFactory() {
        return new CollectionItemImageTableCellFactory();
    }

    @Override
    protected NameTableCellFactory getNameTableCellFactory() {
        return new NameTableCellFactory(this, libraryItemRow -> {
            if (libraryItemRow.getSource() instanceof MediaCollectionItem item) {
                return item.itemType().name();
            }
            return null;
        });
    }

    @Override
    protected void configureHeaderLeft(ItemsDescriptor itemsDescriptor, HBox headerLeft) {
        var parent = itemsDescriptor.parent();
        var collectionsHyperLink = new Hyperlink("Collections");
        collectionsHyperLink.setPadding(new Insets(0));
        collectionsHyperLink.setFocusTraversable(false);
        collectionsHyperLink.setOnAction(_ -> controller.showItems(NavigableItemsResolver.resolveCollections()));
        headerLeft.getChildren().addAll(collectionsHyperLink, new Label(": " + parent.getName()));
    }

    @Override
    public void onOpen(TableView<LibraryItemRow> tableView) {
        var items = tableView.getSelectionModel().getSelectedItems();
        if (items.getFirst() instanceof CollectionChildItemRow collectionChildItemRow) {
            var itemMediaType = collectionChildItemRow.getSource().itemType();
            if (MediaItemType.TRACK == itemMediaType) {
                var track = MusicLibrary.getTrackById(Long.parseLong(collectionChildItemRow.getSource().refId()));
                PlayerTrackListManager.enqueue(new TrackListRequest(track));
            }
        } else {
            LOG.warn("Unexpected item type: {}. Expected type: {}", items.getFirst().getClass(), PlaylistItemRow.class);
        }
    }

    @Override
    public Optional<Consumer<List<LibraryItemRow>>> onDelete() {
        return Optional.of(libraryItemRows -> {
            if (libraryItemRows.getFirst() instanceof CollectionChildItemRow collectionChildItemRow) {
                var collectionId = collectionChildItemRow.getSource().collectionId();
                var itemsIds = libraryItemRows.stream()
                        .map(LibraryItemRow::getId)
                        .toList();
                MusicLibrary.deleteCollectionItems(collectionId, itemsIds);
            }
        });
    }
}
