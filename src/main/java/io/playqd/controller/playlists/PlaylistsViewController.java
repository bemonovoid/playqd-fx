package io.playqd.controller.playlists;

import io.playqd.controller.view.TracksView;
import io.playqd.controller.view.menuitem.TrackRowContextMenuItemsFactory;
import io.playqd.data.PlaylistWithTrackIds;
import io.playqd.player.PlayerTrackListManager;
import io.playqd.player.TrackListRequest;
import io.playqd.service.MusicLibrary;
import io.playqd.utils.Numbers;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;

public class PlaylistsViewController {

    private static final Logger LOG = LoggerFactory.getLogger(PlaylistsViewController.class);

    @FXML
    private ListView<PlaylistWithTrackIds> listView;

    @FXML
    private TracksView tracksView;

    @FXML
    private Label playlistsInfoLabel;

    @FXML
    private void initialize() {
        initPlaylistsInfoLabelListener();
        var tracksTableView = tracksView.tracksTableView();
        initTracksTableViewEventHandlers();
        listView.setCellFactory(new PlaylistsListViewCellFactory());
        initPlaylists();
        listView.getSelectionModel().selectedItemProperty().addListener((_, _, selectedPlaylist) -> {
            if (selectedPlaylist == null) {
                return;
            }
            tracksTableView.showTracks(() -> MusicLibrary.getTracksById(selectedPlaylist.trackIds()));
            updateTrackViewHeader(selectedPlaylist);
        });

        tracksTableView.setTrackContextMenuItemsFactory(() -> {
            var factory = new TrackRowContextMenuItemsFactory();
            factory.setPlaylistModifiedCallback(modifiedPlaylist -> {
                tracksTableView.showTracks(() -> MusicLibrary.getTracksById(modifiedPlaylist.trackIds()));
            });
            factory.setThisPlaylist(this::getSelectedPlaylist);
            return factory;
        });

        MusicLibrary.libraryRefreshedEventProperty().addListener((_, _, _) -> {
            listView.getSelectionModel().clearSelection();
            listView.getSelectionModel().select(0);
        });

        listView.getSelectionModel().select(0);
    }

    private void initPlaylists() {
        MusicLibrary.onPlaylistsModified((playlists) -> {
            listView.getItems().clear();
            listView.getItems().addAll(playlists);
        });
        MusicLibrary.getPlaylists();
    }

    private void initTracksTableViewEventHandlers() {
        tracksView.tracksTableView().rowDoubleClickedProperty().addListener((_, _, row) -> {
            if (row != null) {
                PlayerTrackListManager.enqueue(new TrackListRequest(row.track()));
            }
        });
    }

    @FXML
    private void showCreateNewPlaylistDialog() {
        var dialog = new PlaylistDialog();
        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                MusicLibrary.createPlaylist(name);
            }
        });
    }

    @FXML
    private void showConfirmDeleteEmptyPlaylists() {
        var emptyPlaylistIds = MusicLibrary.getPlaylists().stream()
                .filter(p -> p.trackIds().isEmpty()).map(PlaylistWithTrackIds::id).toList();
        if (emptyPlaylistIds.isEmpty()) {
            LOG.warn("Empty playlists were not found. Nothing to delete.");
        } else {
            LOG.info("Found {} empty playlists.", emptyPlaylistIds.size());
        }
        var alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Playlists manager");
        alert.setHeaderText(null);
        alert.setContentText(String.format("Delete %s empty playlist(s)?", emptyPlaylistIds.size()));
        var result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            MusicLibrary.deletePlaylists(emptyPlaylistIds);
            listView.refresh();
        }
    }

    private void initPlaylistsInfoLabelListener() {
        listView.getItems().addListener((ListChangeListener<PlaylistWithTrackIds>) changed -> {
            if (changed.getList() == null || changed.getList().isEmpty()) {
                playlistsInfoLabel.setText("");
            } else {
                var itemsText = changed.getList().size() > 1 ? "playlists" : "playlist";
                playlistsInfoLabel.setText(Numbers.format(changed.getList().size()) + " " + itemsText);
            }
        });
    }

    private void updateTrackViewHeader(PlaylistWithTrackIds playlist) {
        tracksView.tracksTableHeader().setTitle("Playlist: " + playlist.name());
        var lmdLabel = new Label("Last modified: ");
        var lmdLabelValue = new Label(playlist.lastModifiedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        var hBox = new HBox(lmdLabel, lmdLabelValue);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(5);
        hBox.setDisable(true);
        tracksView.tracksTableHeader().setDetails(hBox);
    }

    public PlaylistWithTrackIds getSelectedPlaylist() {
        return listView.getSelectionModel().getSelectedItem();
    }

    public int getSelectedPlaylistIndex() {
        return listView.getSelectionModel().getSelectedIndex();
    }

    public void refreshPlaylistAtIndex(int listViewItemIndex) {
        listView.getSelectionModel().select(listViewItemIndex);
    }

}
