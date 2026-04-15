package io.playqd.mini.controller.configurer;

import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.custom.AlbumsHeaderMenuButton;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

public final class ArtistTracksViewConfigurer extends TracksViewConfigurer {

    public ArtistTracksViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    protected void configureHeaderRight(TableView<LibraryItemRow> tableView, HBox headerRight) {
        headerRight.getChildren().addAll(
                new AlbumsHeaderMenuButton("showAllTracksToggle", () -> tableView.getItems().getFirst()));
    }

}
