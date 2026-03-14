package io.playqd.controller.trackexplorer;

import io.playqd.controller.view.TracksTableView;
import io.playqd.controller.view.TracksView;
import io.playqd.data.Track;
import io.playqd.player.PlayRequest;
import io.playqd.player.Player;
import io.playqd.service.MusicLibrary;
import io.playqd.service.TrackComparators;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;

public class TracksExplorerViewController {

    @FXML
    private ListView<ListItem> listView;

    @FXML
    private TracksView tracksView;

    private TracksTableView tracksTableView;

    @FXML
    private void initialize() {
        tracksTableView = tracksView.tracksTableView();
        setTracksVisibleColumns();
        initTracksTableViewEventHandlers();
        listView.setCellFactory(new TracksExplorerListViewCellFactory());
        populateListView();
        setOnTrackUpdated();
        listView.getSelectionModel().selectedItemProperty().addListener((_, _, selectedItem) -> {
            if (selectedItem == null) {
                return;
            }
            if (ListItemId.ALL == selectedItem.id()) {
                tracksTableView.showTracks(MusicLibrary::getAllTracks);
            } else if (ListItemId.FAVORITES == selectedItem.id()) {
                tracksTableView.showTracks(MusicLibrary::getFavoriteTracks, TrackComparators.byFavoriteAddedDateDesc());
            } else if (ListItemId.PLAYED == selectedItem.id()) {
                tracksTableView.showTracks(MusicLibrary::getPlayedTracks);
            } else if (ListItemId.CUE == selectedItem.id()) {
                tracksTableView.showTracks(MusicLibrary::getCueTracks);
            }
        });
        listView.getSelectionModel().select(0);
    }

    private void setOnTrackUpdated() {
        MusicLibrary.updatedTrackProperty().addListener((_, _, updatedTrack) -> {
            listView.getItems().setAll(getListItems());
            tracksTableView.refresh();
        });
    }

    private void populateListView() {
        listView.getItems().addAll(getListItems());
    }

    private List<ListItem> getListItems() {
        var counts = getCounts();
        return List.of(
                new ListItem(ListItemId.ALL, "All", counts.allTracks()),
                new ListItem(ListItemId.FAVORITES, "Favorites", counts.favorites()),
                new ListItem(ListItemId.PLAYED, "Played", counts.played()),
                new ListItem(ListItemId.CUE, "Cue tracks", counts.cues()));
    }

    private void setTracksVisibleColumns() {
        tracksTableView.artistCol.setVisible(true);
        tracksTableView.albumCol.setVisible(true);
        tracksTableView.extensionCol.setVisible(true);
        tracksTableView.sizeCol.setVisible(true);
        tracksTableView.ratingCol.setVisible(true);
        tracksTableView.playCountCol.setVisible(true);
        tracksTableView.bitsPerSampleCol.setVisible(true);
        tracksTableView.bitRateCol.setVisible(true);
        tracksTableView.sampleRateCol.setVisible(true);
    }

    private void initTracksTableViewEventHandlers() {
        tracksTableView.rowDoubleClickedProperty().addListener((_, _, row) -> {
            if (row != null) {
                Player.enqueueAndPlay(new PlayRequest(row.track()));
            }
        });
    }

    private static Counts getCounts() {
        var tracks = MusicLibrary.getAllTracksExcludingCueParent();
        var favorites = 0;
        var played = 0;
        var cues = 0;
        for (Track track : tracks) {
            if (track.rating() != null && track.rating().value() > 0) {
                favorites++;
            }
            if (track.playback() != null && track.playback().count() > 0) {
                played++;
            }
            if (track.cueInfo().parentId() != null) {
                cues++;
            }
        }
        return new Counts(tracks.size(), favorites, played, cues);
    }

    private record Counts(int allTracks, int favorites, int played, int cues) {

    }
}
