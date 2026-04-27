package io.playqd.mini.controller.configurer;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

import io.playqd.data.Track;
import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.QueuedTrackItemRow;
import io.playqd.mini.controller.item.contextmenu.ContextMenuItemsBuilder;
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

    @Override
    public Optional<Consumer<List<LibraryItemRow>>> onDelete() {
        return Optional.of(libraryItemRows -> {
            if (libraryItemRows.getFirst() instanceof QueuedTrackItemRow) {
                var tracks = libraryItemRows.stream()
                        .map(item -> (Track) item.getSource())
                        .toList();
                Player.remove(tracks);
            }
        });
    }

    @Override
    public List<MenuItem> configureContextMenuItems(List<LibraryItemRow> selectedItems) {
        var items = selectedItems.stream().map(i -> (Track) i.getSource()).toList();
        return ContextMenuItemsBuilder.newBuilder(controller)
                .playMenuItems(items)
                .removeFromQueueMenuItems(items)
                .separatorMenuItem()
                .playlistMenuItems(items)
                .collectionsMenuItems(selectedItems)
                .separatorMenuItem()
                .showInContextMenuItems(selectedItems)
                .build();
    }

}
