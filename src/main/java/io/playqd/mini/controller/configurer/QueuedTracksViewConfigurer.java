package io.playqd.mini.controller.configurer;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.QueuedTrackItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import io.playqd.player.Player;

public final class QueuedTracksViewConfigurer extends TracksViewConfigurer {

    public QueuedTracksViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    protected void configureHeaderLeft(ItemsDescriptor itemsDescriptor, HBox headerLeft) {
        headerLeft.getChildren().clear();
        headerLeft.getChildren().add(new Label("Queue:"));
    }

    @Override
    public void onOpen(TableView<LibraryItemRow> tableView) {
        var items = tableView.getSelectionModel().getSelectedItems();
        if (items.isEmpty()) {
            return;
        }
        if (items.getFirst() instanceof QueuedTrackItemRow queuedTrackItemRow) {
            Player.play(queuedTrackItemRow.getSource());
        }
    }

}
