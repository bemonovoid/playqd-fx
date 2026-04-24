package io.playqd.mini.controller.configurer;

import java.util.List;
import java.util.Set;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.playqd.data.Album;
import io.playqd.mini.controller.ItemsTableColumnIds;
import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.NavigableItemsResolver;
import io.playqd.mini.controller.factories.AlbumImageTableCellFactory;
import io.playqd.mini.controller.factories.ImageTableCellFactory;
import io.playqd.mini.controller.factories.MiscValueTableCellFactory;
import io.playqd.mini.controller.factories.NameTableCellFactory;
import io.playqd.mini.controller.factories.TracksCountTableCellFactory;
import io.playqd.mini.controller.item.AlbumItemRow;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;

public sealed class AlbumsViewConfigurer extends DefaultItemsViewConfigurer permits ArtistAlbumsViewConfigurer {

    private final static Logger LOG = LoggerFactory.getLogger(AlbumsViewConfigurer.class);

    private MiscValueTableCellFactory miscValueTableCellFactory;

    public AlbumsViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    public void onOpen(TableView<LibraryItemRow> tableView) {
        var items = tableView.getSelectionModel().getSelectedItems();
        if (items.getFirst() instanceof AlbumItemRow albumItemRow) {
            controller.showItems(NavigableItemsResolver.resolveAlbumTracks(albumItemRow));
        }
    }

    @Override
    protected ImageTableCellFactory geImageTableCellFactory() {
        return new AlbumImageTableCellFactory();
    }

    @Override
    protected NameTableCellFactory getNameTableCellFactory() {
        return new NameTableCellFactory(this, libraryItemRow -> {
            if (libraryItemRow.getSource() instanceof Album album) {
                return String.format("%s track%s", album.tracksCount(), album.tracksCount() > 1 ? "s" : "");
            }
            return null;
        });
    }

    @Override
    protected MiscValueTableCellFactory getMiscValueTableCellFactory() {
        if (miscValueTableCellFactory == null) {
            miscValueTableCellFactory = new TracksCountTableCellFactory(libraryItemRow -> {
                if (libraryItemRow instanceof AlbumItemRow albumItemRow) {
                    return albumItemRow.getSource().tracksCount();
                }
                LOG.trace("Unexpected row type: {}", libraryItemRow.getClass());
                return -1;
            });
        }
        return miscValueTableCellFactory;
    }

    @Override
    protected void configureHeaderLeft(ItemsDescriptor itemsDescriptor, HBox headerLeft) {
        headerLeft.getChildren().add(new Label("Albums:"));
    }

}
