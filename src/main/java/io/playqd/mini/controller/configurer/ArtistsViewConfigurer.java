package io.playqd.mini.controller.configurer;

import java.util.List;
import java.util.Set;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.playqd.data.Artist;
import io.playqd.data.Tuple;
import io.playqd.mini.controller.ItemsTableColumnIds;
import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.NavigableItemsResolver;
import io.playqd.mini.controller.factories.AlbumsAndTracksCountTableCellFactory;
import io.playqd.mini.controller.factories.ArtistImageTableCellFactory;
import io.playqd.mini.controller.factories.ImageTableCellFactory;
import io.playqd.mini.controller.factories.MiscValueTableCellFactory;
import io.playqd.mini.controller.factories.NameTableCellFactory;
import io.playqd.mini.controller.item.ArtistItemRow;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;

public final class ArtistsViewConfigurer extends DefaultItemsViewConfigurer {

    private final static Logger LOG = LoggerFactory.getLogger(ArtistsViewConfigurer.class);

    public ArtistsViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    public void onOpen(TableView<LibraryItemRow> tableView) {
        var items = tableView.getSelectionModel().getSelectedItems();
        if (items.getFirst() instanceof ArtistItemRow artistItemRow) {
            controller.showItems(NavigableItemsResolver.resolveArtistAlbums(artistItemRow));
        } else {
            LOG.warn("Unexpected item type: {}. Expected type: {}", items.getFirst().getClass(), ArtistItemRow.class);
        }
    }

    @Override
    protected ImageTableCellFactory geImageTableCellFactory() {
        return new ArtistImageTableCellFactory();
    }

    @Override
    protected NameTableCellFactory getNameTableCellFactory() {
        return new NameTableCellFactory(this, libraryItemRow -> {
            if (libraryItemRow.getSource() instanceof Artist artist) {
                return String.format("%s album%s", artist.albumsCount(), artist.albumsCount() > 1 ? "s" : "");
            }
            return null;
        });
    }

    @Override
    protected MiscValueTableCellFactory getMiscValueTableCellFactory() {
        return new AlbumsAndTracksCountTableCellFactory(libraryItemRow -> {
            if (libraryItemRow instanceof ArtistItemRow artistItemRow) {
                var artist = artistItemRow.getSource();
                return Tuple.unaryFrom(artist.albumsCount(), artist.tracksCount());
            }
            LOG.trace("Unexpected row type: {}", libraryItemRow.getClass());
            return Tuple.empty();
        });
    }

    @Override
    protected void configureHeaderLeft(ItemsDescriptor itemsDescriptor, HBox headerLeft) {
        headerLeft.getChildren().add(new Label("Artists:"));
    }
}
