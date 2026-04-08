package io.playqd.controller.collections;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;

public class CollectionItemRowContextMenu extends ContextMenu {

    private final TableRow<CollectionItemRow> row;

    public CollectionItemRowContextMenu(TableRow<CollectionItemRow> row) {
        this.row = row;
        setMenuItems();
    }

    private void setMenuItems() {
        var removeMenuItem = new MenuItem("Remove", new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
        this.setOnShown(_ -> {
            var selectedItems = row.getTableView().getSelectionModel().getSelectedItems();
            if (selectedItems.size() > 1) {
                removeMenuItem.setText("Remove " +  selectedItems.size() + " items");
            }
        });
        removeMenuItem.setOnAction(_ -> {
            var selectedItems = row.getTableView().getSelectionModel().getSelectedItems();
            if (selectedItems != null && !selectedItems.isEmpty()) {
                var ids = selectedItems.stream().map(i -> i.item().id()).toList();
                MusicLibrary.deleteCollectionItems(selectedItems.getFirst().item().collectionId(), ids);
            }
        });
        getItems().add(removeMenuItem);
    }
}
