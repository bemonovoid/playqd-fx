package io.playqd.controller.trackexplorer;

import io.playqd.controller.view.TracksTableView;
import io.playqd.controller.view.TracksView;
import io.playqd.data.Reaction;
import io.playqd.data.Track;
import io.playqd.player.PlayerTrackListManager;
import io.playqd.player.TrackListRequest;
import io.playqd.service.MusicLibrary;
import javafx.application.Platform;
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
        listView.setCellFactory(new TracksExplorerListViewCellFactory());
        setEventHandlers();
        populateListView();
        listView.getSelectionModel().selectedItemProperty().addListener((_, _, selectedItem) -> {
            if (selectedItem == null) {
                return;
            }
            if (ListItemId.ALL == selectedItem.id()) {
                tracksView.showTracks(MusicLibrary::getAllTracks);
            } else if (ListItemId.LIKES == selectedItem.id()) {
                tracksView.showTracks(() -> MusicLibrary.getReactedTracks(Reaction.THUMB_UP));
            } else if (ListItemId.DISLIKES == selectedItem.id()) {
                tracksView.showTracks(() -> MusicLibrary.getReactedTracks(Reaction.THUMB_DOWN));
            } else if (ListItemId.PLAYED == selectedItem.id()) {
                tracksView.showTracks(MusicLibrary::getPlayedTracks);
            } else if (ListItemId.CUE == selectedItem.id()) {
                tracksView.showTracks(MusicLibrary::getCueTracks);
            }
        });
        listView.getSelectionModel().select(0);
    }

    @FXML
    private void refresh() {
        Platform.runLater(() -> {
            listView.setDisable(true);
            MusicLibrary.refresh();
        });
    }

    private void populateListView() {
        listView.getItems().addAll(buildListItems());
    }

    private static List<ListItem> buildListItems() {
        var counts = getCounts();
        return List.of(
                new ListItem(ListItemId.ALL, "All", new SimpleIntegerProperty(counts.allTracks())),
                new ListItem(ListItemId.LIKES, "Liked", new SimpleIntegerProperty(counts.likes())),
                new ListItem(ListItemId.DISLIKES, "Disliked", new SimpleIntegerProperty(counts.dislikes())),
                new ListItem(ListItemId.PLAYED, "Played", new SimpleIntegerProperty(counts.played())),
                new ListItem(ListItemId.CUE, "Cue tracks", new SimpleIntegerProperty(counts.cues())));
    }

    private void setEventHandlers() {
        tracksTableView.rowDoubleClickedProperty().addListener((_, _, row) -> {
            if (row != null) {
                PlayerTrackListManager.enqueue(new TrackListRequest(row.track()));
            }
        });
        setMusicLibraryEventListeners();
    }

    private void setMusicLibraryEventListeners() {
        MusicLibrary.libraryRefreshedEventProperty().addListener((_, _, _) -> {
            var selectedIdx = listView.getSelectionModel().getSelectedIndex();
            if (selectedIdx < 0) {
                selectedIdx = 0;
            }
            listView.getItems().clear();
            listView.getItems().addAll(buildListItems());
            listView.getSelectionModel().select(selectedIdx);
            listView.setDisable(false);
        });
        MusicLibrary.updatedTracksProperty().addListener((_, _, tracksUpdatedEvent) -> {
            var counts = getCounts();
            listView.getItems().forEach(li -> {
                switch (li.id()) {
                    case ALL -> li.countProperty().set(counts.allTracks());
                    case CUE -> li.countProperty().set(counts.cues());
                    case LIKES -> {
                        li.countProperty().set(counts.likes());
//                        if (li == getSelectedItem() && TrackUpdateType.REACTION == tracksUpdatedEvent.type()) {
//                            tracksView.showTracks(() -> MusicLibrary.getReactedTracks(Reaction.THUMB_UP));
//                        }
                    }
                    case DISLIKES -> {
//                        li.countProperty().set(counts.dislikes());
//                        if (li == getSelectedItem() && TrackUpdateType.REACTION == tracksUpdatedEvent.type()) {
//                            tracksView.showTracks(() -> MusicLibrary.getReactedTracks(Reaction.THUMB_DOWN));
//                        }
                    }
                    case PLAYED -> {
//                        li.countProperty().set(counts.played());
//                        if (li == getSelectedItem() && TrackUpdateType.PLAY_COUNT_INCR == tracksUpdatedEvent.type()) {
//                            tracksView.showTracks(MusicLibrary::getPlayedTracks);
//                        }
                    }
                }
            });
        });
    }

    private ListItem getSelectedItem() {
        return listView.getSelectionModel().getSelectedItem();
    }

    private static Counts getCounts() {
        var tracks = MusicLibrary.getAllTracksExcludingCueParent();
        var likes = 0;
        var dislikes = 0;
        var played = 0;
        var cues = 0;
        for (Track track : tracks) {
            if (Reaction.THUMB_UP == track.reaction()) {
                likes++;
            }
            if (Reaction.THUMB_DOWN == track.reaction()) {
                dislikes++;
            }
            if (track.playback() != null && track.playback().count() > 0) {
                played++;
            }
            if (track.isCueTrack()) {
                cues++;
            }
        }
        return new Counts(tracks.size(), likes, dislikes, played, cues);
    }

    private record Counts(int allTracks, int likes, int dislikes, int played, int cues) {

    }
}
