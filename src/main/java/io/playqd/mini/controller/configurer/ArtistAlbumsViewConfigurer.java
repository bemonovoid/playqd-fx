package io.playqd.mini.controller.configurer;

import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.factories.DescriptionTableCellFactory;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.custom.AlbumsHeaderMenuButton;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

public final class ArtistAlbumsViewConfigurer extends AlbumsViewConfigurer {

    public ArtistAlbumsViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    protected DescriptionTableCellFactory getDescriptionTableCellFactory() {
        return null;
    }

    @Override
    protected void configureHeaderRight(TableView<LibraryItemRow> tableView, HBox headerRight) {
        headerRight.getChildren().addAll(
                new AlbumsHeaderMenuButton("showAllAlbumsToggle", () -> tableView.getItems().getFirst()));
    }
}
