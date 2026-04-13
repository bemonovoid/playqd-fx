package io.playqd.mini.controller.configurer;

import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.item.LibraryItemRow;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

public final class QueuedTracksItemsViewConfigurer extends TracksItemsViewConfigurer {

    public QueuedTracksItemsViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    protected void configureHeaderLeft(TableView<LibraryItemRow> tableView, HBox headerLeft) {
        headerLeft.getChildren().clear();
        headerLeft.getChildren().add(new Label("Queue:"));
    }

}
