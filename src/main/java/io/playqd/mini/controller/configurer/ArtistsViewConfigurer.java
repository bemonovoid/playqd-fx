package io.playqd.mini.controller.configurer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.playqd.data.Artist;
import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.NavigableItemsResolver;
import io.playqd.mini.controller.factories.ArtistImageTableCellFactory;
import io.playqd.mini.controller.factories.ImageTableCellFactory;
import io.playqd.mini.controller.factories.NameTableCellFactory;
import io.playqd.mini.controller.item.ArtistItemRow;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import io.playqd.service.MusicLibrary;
import io.playqd.service.TrackComparators;

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
    protected void configureHeaderLeft(ItemsDescriptor itemsDescriptor, HBox headerLeft) {
        headerLeft.getChildren().add(new Label("Artists:"));
    }

    @Override
    public Supplier<List<MenuItem>> configureViewOptionsMenuItems(TableView<LibraryItemRow> tableView) {
        return () -> {
            Comparator<LibraryItemRow> comp = (a1, a2) -> {
                if (a1.getSource() instanceof Artist artist1 && a2.getSource() instanceof Artist artist2) {
                    var a1Date = MusicLibrary.getArtistTracks(artist1.id()).stream()
                            .max(TrackComparators.byAddedDate())
                            .map(t -> t.fileAttributes().createdDate())
                            .orElse(LocalDateTime.now());
                    var a2Date = MusicLibrary.getArtistTracks(artist2.id()).stream()
                            .max(TrackComparators.byAddedDate())
                            .map(t -> t.fileAttributes().createdDate())
                            .orElse(LocalDateTime.now());
                    return a2Date.compareTo(a1Date);
                }
                return 0;
            };
            return List.of(); //todo finish Sort By added date
        };
    }

}
