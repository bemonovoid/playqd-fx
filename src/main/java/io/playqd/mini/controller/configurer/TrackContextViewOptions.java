package io.playqd.mini.controller.configurer;

import io.playqd.mini.controller.NavigableItemsResolver;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.events.NavigationEvent;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableView;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

final class TrackContextViewOptions {

    private final Menu filterByMenu;
    private final Menu sortByMenu;

    TrackContextViewOptions(TableView<LibraryItemRow> tableView) {
        this.filterByMenu = createFilterByMenu(tableView);
        this.sortByMenu = createSortByMenu(tableView);
    }

    Menu getFilterByMenu() {
        return filterByMenu;
    }

    Menu getSortByMenu() {
        return sortByMenu;
    }

    private static Menu createFilterByMenu(TableView<LibraryItemRow> tableView) {
        var clearFilters = new MenuItem("Clear filters");
        clearFilters.setGraphic(FontIcon.of(FontAwesomeSolid.TIMES));
        clearFilters.setOnAction(_ -> tableView.fireEvent(new NavigationEvent(NavigableItemsResolver.allTracks())));

        var cue = new MenuItem(".cue tracks");
        cue.setGraphic(new FontIcon("far-file-alt"));
        cue.setOnAction(_ -> tableView.fireEvent(new NavigationEvent(NavigableItemsResolver.cueTracks())));

        var cueParent = new MenuItem(".cue parent tracks");
        cueParent.setGraphic(new FontIcon("far-file-alt"));
        cueParent.setOnAction(_ -> tableView.fireEvent(new NavigationEvent(NavigableItemsResolver.cueParentTracks())));

        var liked = new MenuItem("Liked");
        liked.setGraphic(new FontIcon("far-heart"));

        var played = new MenuItem("Played");
        played.setGraphic(new FontIcon("fas-play"));

        var filterByMenu = new Menu("Filter");
        filterByMenu.setGraphic(new FontIcon("fas-filter"));

        filterByMenu.getItems().addAll(
                clearFilters,
                new SeparatorMenuItem(),
                cue, cueParent,
                new SeparatorMenuItem(),
                liked, played);

        return filterByMenu;
    }

    private static Menu createSortByMenu(TableView<LibraryItemRow> tableView) {
        var sortByMenu = new Menu("Sort by");
        sortByMenu.setGraphic(new FontIcon("fas-sort"));
        return sortByMenu;
    }
}
