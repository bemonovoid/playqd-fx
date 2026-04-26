package io.playqd.mini.controller.configurer;

import java.util.function.Supplier;

import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import org.kordamp.ikonli.javafx.FontIcon;

import io.playqd.mini.controller.NavigableItemsResolver;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.events.NavigationEvent;

class ArtistContextViewOptions {

    private final MenuItem showAllAlbumsToggle;
    private final MenuItem showAllTracksToggle;

    ArtistContextViewOptions(Node firingEventNode,
                             ArtistChildrenKind childrenKind,
                             Supplier<LibraryItemRow> libraryItemRow) {
        this.showAllAlbumsToggle = new MenuItem("Show all albums");
        showAllAlbumsToggle.setGraphic(ArtistChildrenKind.ALBUMS == childrenKind ? new FontIcon("fas-check") : null);
        this.showAllAlbumsToggle.setOnAction(_ -> firingEventNode.fireEvent(
                new NavigationEvent(NavigableItemsResolver.resolveArtistAlbums(libraryItemRow.get()))));

        this.showAllTracksToggle = new MenuItem("Show all tracks");
        showAllTracksToggle.setGraphic(ArtistChildrenKind.TRACKS == childrenKind ? new FontIcon("fas-check") : null);
        this.showAllTracksToggle.setOnAction(_ -> firingEventNode.fireEvent(
                new NavigationEvent(NavigableItemsResolver.resolveArtistTracks(libraryItemRow.get()))));
    }

    MenuItem getShowAllAlbumsMenuItem() {
        return showAllAlbumsToggle;
    }

    MenuItem getShowAllTracksMenuItem() {
        return showAllTracksToggle;
    }

    enum ArtistChildrenKind {
        ALBUMS, TRACKS;
    }

}
