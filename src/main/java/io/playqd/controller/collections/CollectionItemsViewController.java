package io.playqd.controller.collections;

import io.playqd.controller.view.TrackRowContextMenu;
import io.playqd.controller.view.TrackSelectedRow;
import io.playqd.controller.view.TrackTableRow;
import io.playqd.data.MediaCollection;
import io.playqd.data.MediaCollectionItem;
import io.playqd.event.MouseEventHelper;
import io.playqd.player.PlayerTrackListManager;
import io.playqd.player.TrackListRequest;
import io.playqd.service.MusicLibrary;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;

public class CollectionItemsViewController {

    @FXML
    private Label titleLabel, lastModifiedDateLabel, selectedLabel, detailsLabel;

    @FXML
    private TableView<MediaCollectionItem> tableView;

    @FXML
    private TableColumn<MediaCollectionItem, String> iconCol, titleCol, itemTypeCol, commentCol;

    @FXML
    private void initialize() {
        initTableProperties();
        intiColumnCellFactories();
        initColumnCellValueFactories();
        initRowFactory();
    }

    private void initTableProperties() {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private void intiColumnCellFactories() {
        iconCol.setCellFactory(new CollectionItemIconTableCellFactory());
    }

    private void initColumnCellValueFactories() {
        iconCol.setCellValueFactory(c -> new SimpleObjectProperty<>(""));
        titleCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().title()));
        itemTypeCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().itemType().name()));
        commentCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().comment()));
    }

    private void initRowFactory() {
        tableView.setRowFactory(_ -> {
            var row = new TableRow<MediaCollectionItem>();
            setOnRowMouseClicked(row);
            return row;
        });
    }

    private void setOnRowMouseClicked(TableRow<MediaCollectionItem> row) {
        row.setOnMouseClicked(e -> {
            if (!row.isEmpty()) {
                if (MouseEventHelper.primaryButtonDoubleClicked(e)) {
                    var selectedItem = row.getItem();
                    onItemDoubleClicked(selectedItem);
                } else if (MouseEventHelper.secondaryButtonSingleClicked(e)) {
                    if (row.getContextMenu() == null) {
//                        var contextMenu = new TrackRowContextMenu(getTrackContextMenuItemsFactory());
//                        contextMenu.setOnHidden(_ -> row.setContextMenu(null)); // to reset a state
//                        row.setContextMenu(contextMenu);
//                        contextMenu.show(row, e.getScreenX(), e.getScreenY());
                    }
                }
            }
        });
    }

    private void onItemDoubleClicked(MediaCollectionItem item) {
        switch (item.itemType()) {
            case ARTIST -> {
            }
            case ALBUM -> {
            }
            case TRACK -> {
                var trackId = Long.parseLong(item.refId());
                PlayerTrackListManager.enqueue(new TrackListRequest(MusicLibrary.getTrackById(trackId)));
            }
            case PLAYLIST -> {
            }
            case ARTWORK -> {
            }
            case CUE_FILE -> {
            }
            case FILE -> {
            }
        }
    }

    void showItems(MediaCollection collection) {
        tableView.setDisable(collection.items().isEmpty());
        updateTableHeader(collection);
        tableView.setUserData(collection.items());
        tableView.setItems(FXCollections.observableList(collection.items()));
        tableView.scrollTo(0);

    }

    private void updateTableHeader(MediaCollection collection) {
        titleLabel.setText(collection.name());
        lastModifiedDateLabel.setText(collection.lastModifiedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
}
