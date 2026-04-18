package io.playqd.mini.controller.configurer;

import io.playqd.data.Tuple;
import io.playqd.mini.controller.ItemsTableColumnIds;
import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.NavigableItemsResolver;
import io.playqd.mini.controller.factories.AlbumsAndTracksCountTableCellFactory;
import io.playqd.mini.controller.factories.ArtistImageTableCellFactory;
import io.playqd.mini.controller.factories.ImageTableCellFactory;
import io.playqd.mini.controller.factories.MiscValueTableCellFactory;
import io.playqd.mini.controller.item.ArtistItemRow;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public final class ArtistsViewConfigurer extends DefaultItemsViewConfigurer {

    private final static Logger LOG = LoggerFactory.getLogger(ArtistsViewConfigurer.class);

    public ArtistsViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    protected Set<String> getExcludedColumns() {
        return Set.of(ItemsTableColumnIds.DESCRIPTION_COL);
    }

    @Override
    public void onItemsOpen(List<LibraryItemRow> items) {
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
