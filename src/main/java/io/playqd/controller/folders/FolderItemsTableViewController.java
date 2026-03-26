package io.playqd.controller.folders;

import io.playqd.data.WatchFolderItem;
import io.playqd.event.MouseEventHelper;
import io.playqd.platform.PlatformApi;
import io.playqd.player.PlayerTrackListManager;
import io.playqd.player.TrackListRequest;
import io.playqd.service.MusicLibrary;
import io.playqd.utils.DateUtils;
import io.playqd.utils.ImagePopup;
import io.playqd.utils.Numbers;
import io.playqd.utils.PlayqdApis;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FolderItemsTableViewController {

    private static final Logger LOG = LoggerFactory.getLogger(FolderItemsTableViewController.class);

    @FXML
    private Label titleLabel, selectedLabel, detailsLabel;

    @FXML
    private TableView<WatchFolderItem> tableView;

    @FXML
    public TableColumn<WatchFolderItem, String> filenameCol, sizeCol, mimeTypeCol, createdDataCol, lastModifiedDataCol;

    @FXML
    private void initialize() {
        setRowFactory();
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tableView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<WatchFolderItem>) changed -> {
            var items = changed == null ? Collections.<WatchFolderItem>emptyList() : changed.getList();
            updateSelectedLabel(items);
        });

        filenameCol.setCellFactory(new FilenameTableViewColumnCellFactory());
        filenameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name()));
        mimeTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().mimeType()));
        sizeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().displaySize()));
        createdDataCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().createdDate() == null ? "" :
                DateUtils.ldtFormatted(c.getValue().createdDate())));
        lastModifiedDataCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().lastModifiedDate() == null ?
                "" : DateUtils.ldtFormatted(c.getValue().lastModifiedDate())));
    }

    void setItems(List<WatchFolderItem> items) {
        tableView.setItems(FXCollections.observableList(items));
        tableView.refresh();
        if (!items.isEmpty()) {
            var item = items.getFirst();
            titleLabel.setText(item.path().getParent().toString());
        }
        updateDetailsLabel();
    }

    private void setRowFactory() {
        tableView.setRowFactory(_ -> {
            var row = new TableRow<WatchFolderItem>();
            row.setOnMouseClicked(e -> {
                if (MouseEventHelper.primaryButtonDoubleClicked(e)) {
                    var selectedItems = tableView.getSelectionModel().getSelectedItems();
                    if (selectedItems.size() == 1) {
                        var wfi = selectedItems.getFirst();
                        var mimeType = wfi.mimeType().toLowerCase();
                        if (!mimeType.startsWith("audio")) {
                            var location = PlayqdApis.watchFolderItemBinary(wfi.id());
                            PlatformApi.open(location);
                            return;
                        }
                    }
                    var selectedItemPaths = selectedItems.stream()
                            .filter(wfi -> wfi.mimeType().toLowerCase().startsWith("audio"))
                            .map(WatchFolderItem::path)
                            .collect(Collectors.toSet());
                    var tracks = MusicLibrary.getTracksByPaths(selectedItemPaths);
                    PlayerTrackListManager.enqueue(new TrackListRequest(tracks, 0));
                }
            });
            return row;
        });
    }

    private void updateSelectedLabel(List<? extends WatchFolderItem> selectedItems) {
        var selected = selectedItems == null ? 0 : selectedItems.size();
        var text = String.format("Selected: %s", selected);
        selectedLabel.setText(text);
    }

    private void updateDetailsLabel() {
        var items = tableView.getItems();
        if (items == null || items.isEmpty()) {
            detailsLabel.setText("");
        } else {
            var totalSize = (long) 0;
            for (var item : items) {
                totalSize += item.size() == null ? 0 : item.size();
            }
            var files = Numbers.format(items.size()) + (items.size() > 1 ? " files" : " file");
            var sizeFormatted = org.apache.commons.io.FileUtils.byteCountToDisplaySize(totalSize);
            detailsLabel.setText(String.format("%s, %s", files, sizeFormatted));
        }
    }
}
