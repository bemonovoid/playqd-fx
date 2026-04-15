package io.playqd.mini.controller.configurer;

import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.NavigableItemsResolver;
import io.playqd.mini.controller.factories.*;
import io.playqd.mini.controller.item.AlbumItemRow;
import io.playqd.mini.controller.item.ArtistAlbumItemRow;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import io.playqd.mini.custom.AlbumsHeaderMenuButton;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public sealed class AlbumsViewConfigurer extends DefaultItemsViewConfigurer permits ArtistAlbumsViewConfigurer {

    private final static Logger LOG = LoggerFactory.getLogger(AlbumsViewConfigurer.class);

    private MiscValueTableCellFactory miscValueTableCellFactory;

    public AlbumsViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    public void onRowOpened(LibraryItemRow item) {
        if (item instanceof AlbumItemRow albumItemRow) {
            controller.showItems(NavigableItemsResolver.resolveAlbumTracks(albumItemRow));
        }
    }

    @Override
    protected ImageTableCellFactory geImageTableCellFactory() {
        return new AlbumImageTableCellFactory();
    }

    @Override
    protected DescriptionTableCellFactory getDescriptionTableCellFactory() {
        return new HyperLinkTableCellFactory(NavigableItemsResolver::resolveArtistAlbums);
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

    @Override
    protected void configureHeaderRight(TableView<LibraryItemRow> tableView, HBox headerRight) {

    }
}
