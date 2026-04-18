package io.playqd.mini.controller.configurer;

import io.playqd.mini.controller.item.LibraryItemRow;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
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
        var cue = new MenuItem("CUE track");
        cue.setGraphic(new FontIcon("far-file-alt"));

        var liked = new MenuItem("Liked");
        liked.setGraphic(new FontIcon("far-heart"));

        var played = new MenuItem("Played");
        played.setGraphic(new FontIcon("fas-play"));

        var filterByMenu = new Menu("Filter");
        filterByMenu.setGraphic(new FontIcon("fas-filter"));

        filterByMenu.getItems().addAll(cue, liked, played);

        return filterByMenu;
    }

    private static Menu createSortByMenu(TableView<LibraryItemRow> tableView) {
        var sortByMenu = new Menu("Sort");
        sortByMenu.setGraphic(new FontIcon("fas-sort"));
        return sortByMenu;
    }
}
