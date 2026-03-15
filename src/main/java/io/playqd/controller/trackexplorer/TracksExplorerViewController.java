package io.playqd.controller.trackexplorer;

import io.playqd.controller.view.TracksTableView;
import io.playqd.controller.view.TracksView;
import io.playqd.controller.view.menuitem.TracksExplorerTrackContextMenuConfigurer;
import io.playqd.data.Track;
import io.playqd.event.TrackUpdateType;
import io.playqd.player.PlayRequest;
import io.playqd.player.Player;
import io.playqd.service.MusicLibrary;
import javafx.beans.property.SimpleIntegerProperty;
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
        tracksTableView.setTrackContextMenuConfigurerFactory(() -> new TracksExplorerTrackContextMenuConfigurer(this));
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
                tracksTableView.showTracks(MusicLibrary::getFavoriteTracks);
            } else if (ListItemId.PLAYED == selectedItem.id()) {
                tracksTableView.showTracks(MusicLibrary::getPlayedTracks);
            } else if (ListItemId.CUE == selectedItem.id()) {
                tracksTableView.showTracks(MusicLibrary::getCueTracks);
            }
        });
        listView.getSelectionModel().select(0);
    }

    private void setOnTrackUpdated() {
        MusicLibrary.tracksUpdateEventProperty().addListener((_, _, tracksUpdateEvent) -> {
            var counts = getCounts();
            listView.getItems().forEach(li -> {
                switch (li.id()) {
                    case ALL -> li.countProperty().set(counts.allTracks());
                    case CUE -> li.countProperty().set(counts.cues());
                    case FAVORITES -> {
                        li.countProperty().set(counts.favorites());
                        if (li == getSelectedItem() && tracksUpdateEvent.type().isLikeOrUnlike()) {
                            tracksTableView.showTracks(MusicLibrary::getFavoriteTracks);
                        }
                    }
                    case PLAYED -> {
                        li.countProperty().set(counts.played());
                        if (li == getSelectedItem() && TrackUpdateType.PLAY_COUNT_INCR == tracksUpdateEvent.type()) {
                            tracksTableView.showTracks(MusicLibrary::getPlayedTracks);
                        }
                    }
                }
            });
        });
    }

    private void populateListView() {
        listView.getItems().addAll(buildListItems());
    }

    private static List<ListItem> buildListItems() {
        var counts = getCounts();
        return List.of(
                new ListItem(ListItemId.ALL, "All", new SimpleIntegerProperty(counts.allTracks())),
                new ListItem(ListItemId.FAVORITES, "Favorites", new SimpleIntegerProperty(counts.favorites())),
                new ListItem(ListItemId.PLAYED, "Played", new SimpleIntegerProperty(counts.played())),
                new ListItem(ListItemId.CUE, "Cue tracks", new SimpleIntegerProperty(counts.cues())));
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

    private ListItem getSelectedItem() {
        return listView.getSelectionModel().getSelectedItem();
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
