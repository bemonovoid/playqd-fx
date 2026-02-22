package io.playqd.controller.trackexplorer;

import io.playqd.controller.view.TracksTableView;
import io.playqd.data.Track;
import io.playqd.player.PlayRequest;
import io.playqd.player.PlayerEngine;
import io.playqd.service.TrackComparators;
import io.playqd.service.TracksService;
import io.playqd.utils.TimeUtils;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.time.Duration;

public class TracksExplorerViewController {

    @FXML
    private ListView<ListItem> listView;

    @FXML
    private TracksTableView tracksTableView;

    @FXML
    private Label tracksSelectedLabel, tracksInfoLabel;

    @FXML
    private void initialize() {
        setTracksVisibleColumns();
        setTracksInfoLabel();
        initTracksTableViewEventHandlers();
        listView.setCellFactory(new TracksExplorerListViewCellFactory());
        populateListView();
        listView.getSelectionModel().selectedItemProperty().addListener((_, _, selectedItem) -> {
            if (selectedItem == null) {
                return;
            }
            if (ListItemId.ALL == selectedItem.id()) {
                tracksTableView.showTracks(TracksService::getAllTracks);
            } else if (ListItemId.FAVORITES == selectedItem.id()) {
                tracksTableView.showTracks(TracksService::getAllFavorites, TrackComparators.byFavoriteAddedDateDesc());
            }
        });
        listView.getSelectionModel().select(0);
    }

    private void populateListView() {
        listView.getItems().add(new ListItem(ListItemId.ALL, "All", 0));
        listView.getItems().add(new ListItem(ListItemId.FAVORITES, "Favorites", 0));
        listView.getItems().add(new ListItem(ListItemId.PLAYED, "Played", 0));
    }

    private void setTracksVisibleColumns() {
        tracksTableView.artistCol.setVisible(true);
        tracksTableView.albumCol.setVisible(true);
        tracksTableView.extensionCol.setVisible(true);
        tracksTableView.sizeCol.setVisible(true);
        tracksTableView.ratingCol.setVisible(true);
        tracksTableView.playCountCol.setVisible(true);
    }

    private void setTracksInfoLabel() {
        tracksInfoLabel.textProperty().bind(tracksTableView.tracksInfoProperty());
    }

    private void initTracksTableViewEventHandlers() {
        tracksTableView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Track>) changed -> {
            var selected = changed != null ? changed.getList().size() : 0;
            var time = "";
            if (selected > 0) {
                var totalSeconds = changed.getList().stream().mapToInt(t -> t.length().seconds()).sum();
                time = TimeUtils.durationToTimeFormat(Duration.ofSeconds(totalSeconds));
            }
            var text = String.format("Selected: %s, time: %s", selected, time);
            tracksSelectedLabel.setText(text);
        });
        tracksTableView.rowDoubleClickedProperty().addListener((_, _, row) -> {
            if (row != null) {
                PlayerEngine.enqueueAndPlay(new PlayRequest(row.track()));
            }
        });
    }
}
