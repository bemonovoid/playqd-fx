package io.playqd.controller.collections;

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
import javafx.scene.control.cell.TextFieldTableCell;

import java.time.format.DateTimeFormatter;

public class CollectionItemsViewController {

    @FXML
    private Label titleLabel, lastModifiedDateLabel, selectedLabel, detailsLabel;

    @FXML
    private TableView<CollectionItemRow> tableView;

    @FXML
    private TableColumn<CollectionItemRow, String> iconCol, commentCol;

    @FXML
    private void initialize() {
        initTableProperties();
        intiColumnCellFactories();
        initColumnCellValueFactories();
        initRowFactory();
        setOnEditCommit();
    }

    private void initTableProperties() {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private void intiColumnCellFactories() {
        iconCol.setCellFactory(new CollectionItemIconTableCellFactory());
        commentCol.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    private void initColumnCellValueFactories() {
        iconCol.setCellValueFactory(_ -> new SimpleObjectProperty<>(""));
        commentCol.setCellValueFactory(c -> c.getValue().getComment());
    }

    private void initRowFactory() {
        tableView.setRowFactory(_ -> {
            var row = new TableRow<CollectionItemRow>();
            setOnRowMouseClicked(row);
            return row;
        });
    }

    private void setOnEditCommit() {
        commentCol.setOnEditCommit(event -> {
            var newComment = event.getNewValue();
            var rowItem = event.getRowValue();
            MusicLibrary.updateCollectionItemComment(event.getRowValue().item().id(), newComment);
            rowItem.setComment(newComment);
        });
    }

    private void setOnRowMouseClicked(TableRow<CollectionItemRow> row) {
        row.setOnMouseClicked(e -> {
            if (!row.isEmpty()) {
                if (MouseEventHelper.primaryButtonDoubleClicked(e)) {
                    var selectedItem = row.getItem().item();
                    onItemDoubleClicked(selectedItem);
                } else if (MouseEventHelper.secondaryButtonSingleClicked(e)) {
                    if (row.getContextMenu() == null) {
                        var contextMenu = new CollectionItemRowContextMenu(row);
                        contextMenu.setOnHidden(_ -> row.setContextMenu(null)); // to reset a state
                        row.setContextMenu(contextMenu);
                        contextMenu.show(row, e.getScreenX(), e.getScreenY());
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
        tableView.setItems(FXCollections.observableList(
                collection.items().stream().map(CollectionItemRow::new).toList()));
        tableView.scrollTo(0);
    }

    long getCollectionId() {
        return tableView.getItems().isEmpty() ? -1 : tableView.getItems().getFirst().item().collectionId();
    }

    private void updateTableHeader(MediaCollection collection) {
        titleLabel.setText(collection.name());
        lastModifiedDateLabel.setText(collection.lastModifiedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
}
