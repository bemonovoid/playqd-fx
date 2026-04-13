package io.playqd.mini.controller.configurer;

import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.factories.ArtistImageTableCellFactory;
import io.playqd.mini.controller.item.AlbumItemRow;
import io.playqd.mini.controller.item.ArtistAlbumItemRow;
import io.playqd.mini.controller.item.ArtistItemRow;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import io.playqd.mini.controller.navigator.NavigableItems;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ArtistsItemsViewConfigurer extends DefaultItemsViewConfigurer {

    private final static Logger LOG = LoggerFactory.getLogger(ArtistsItemsViewConfigurer.class);

    public ArtistsItemsViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    public void configureColumns(TableView<LibraryItemRow> tableView) {
        tableView.getColumns()
                .forEach(col -> {
                    if (col.getId().equals("imageCol")) {
                        @SuppressWarnings("unchecked")
                        var imageCol = (TableColumn<LibraryItemRow, Long>) col;
                        imageCol.setCellFactory(new ArtistImageTableCellFactory());
                    }
                });
    }

    @Override
    public void configureFooter(TableView<LibraryItemRow> tableView, Label footerLabel) {
        var items = tableView.getItems();
        if (items.isEmpty()) {
            footerLabel.setText("");
        } else {
            footerLabel.setText(items.size() + " artist" + (items.size() > 1 ? "s" : ""));
        }
    }

    @Override
    public void onItemMouseDoubleClicked(LibraryItemRow item) {
        if (item instanceof ArtistItemRow _) {
            Supplier<List<LibraryItemRow>> albumsSupplier = () -> new ArrayList<>(
                    MusicLibrary.getArtistAlbums(item.getId()).stream()
                            .map(ArtistAlbumItemRow::new)
                            .toList());
            controller.showItems(new NavigableItems(
                    ItemsDescriptor.forArtistAlbums(item.getName()), albumsSupplier, ArtistAlbumItemRow.class));
        } else {
            LOG.error("Unexpected item type: {}. Expected type: {}", item.getClass(), ArtistItemRow.class);
        }
    }

    @Override
    protected void configureHeaderLeft(TableView<LibraryItemRow> tableView, HBox headerLeft) {
        headerLeft.getChildren().add(new Label("Artists:"));
    }

    @Override
    protected void configureHeaderRight(TableView<LibraryItemRow> tableView, HBox headerRight) {

    }
}
