package io.playqd.player;

import io.playqd.client.Images;
import io.playqd.config.AppConfig;
import io.playqd.controller.view.ObservableProperties;
import io.playqd.controller.view.request.CollectionsViewRequest;
import io.playqd.controller.view.request.FoldersViewRequest;
import io.playqd.controller.view.request.PlaylistsViewRequest;
import io.playqd.data.Track;
import io.playqd.service.MusicLibrary;
import io.playqd.utils.DateUtils;
import io.playqd.utils.TimeUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class PlayerTrackListViewController extends PlayerTrackListView {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerTrackListViewController.class);

    @FXML
    private ListView<Track> trackListView;

    @FXML
    private ImageView artistArtImageView, albumArtImageView;

    @FXML
    private Label artistNameLabel, trackTitleLabel, albumNameLabel, albumDetailsLabel, audioFormatDetailsLabel,
            libraryDetailsLabel, createdDateLabel, modifiedDateLabel;

    @FXML
    private Hyperlink locationLabel;

    @FXML
    private HBox playlistsHBox, collectionsHbox;

    @Override
    void setItems(List<Track> tracks) {
        trackListView.setCellFactory(new PlayerTrackListViewCellFactory());
        trackListView.setItems((FXCollections.observableArrayList(new ArrayList<>(tracks))));
        trackListView.refresh();
        trackListView.scrollTo(0);
    }

    @Override
    int addNext(List<Track> tracks) {
        if (trackListView.getItems().isEmpty()) {
            setItems(tracks);
            return 0;
        }
        var playingTrackIdx = Player.playingTrack().map(playingTrack -> trackListView.getItems().indexOf(playingTrack));
        if (playingTrackIdx.isPresent()) {
            var insertIndex = playingTrackIdx.get() + 1;
            trackListView.getItems().addAll(insertIndex, tracks);
            return insertIndex;
        }
        var selectedItemIdx = trackListView.getSelectionModel().getSelectedIndex();
        if (selectedItemIdx < 0) {
            trackListView.getItems().addAll(0, tracks);
            return 0;
        } else {
            var insertIndex = selectedItemIdx + 1;
            trackListView.getItems().addAll(insertIndex, tracks);
            return insertIndex;
        }
    }

    @Override
    int addLast(List<Track> tracks) {
        if (trackListView.getItems().isEmpty()) {
            setItems(tracks);
            return 0;
        }
        var insertIdx = trackListView.getItems().size();
        trackListView.getItems().addAll(insertIdx, tracks);
        return insertIdx;
    }

    @FXML
    private void initialize() {
        trackListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//        PlayerTrackListManager.setPlayerTrackListView(this);
        setOnKeyPressedHandlers();
        Player.onPlayingTrackChanged(playingTrack ->
                Platform.runLater(() ->
                        trackListView.getItems().stream()
                                .filter(track -> track.id() == playingTrack.id())
                                .findFirst()
                                .ifPresent(track -> {
                                    trackListView.getSelectionModel().clearSelection();
                                    trackListView.getSelectionModel().select(track);
                                    updateTrackInfoView(track);
                                })));
        var lastTracklist = new HashSet<>(AppConfig.getProperties().player().state().tracklist());
        if (!lastTracklist.isEmpty()) {
            var tracks = MusicLibrary.getTracksById(lastTracklist);
            var lastPlayedTrackIdProp = AppConfig.getProperties().player().state().lastPlayedTrackId();
            var startIdx = 0;
            if (lastPlayedTrackIdProp != null) {
                var lastPlayedTrackId = lastPlayedTrackIdProp.get();
                for (int i = 0; i < tracks.size(); i++) {
                    if (tracks.get(i).id() == lastPlayedTrackId) {
                        startIdx = i;
                        break;
                    }
                }
            }
            PlayerTrackListManager.enqueueAndPlay(new TrackListRequest(startIdx, tracks, false));
            trackListView.getSelectionModel().select(startIdx);
            updateTrackInfoView(tracks.get(startIdx));
            //TODO update player toolbar
            //TODO handle last played track(s) do no exist
        }
    }

    @FXML
    private void clear() {
        setItems(Collections.emptyList());
        PlayerTrackListManager.clear();
    }

    @FXML
    private void clearAndStopPlayer() {
        clear();
        Player.stop();
    }

    @FXML
    private void showInFolder() {
        var locationStr = locationLabel.getText();
        if (locationStr != null && !locationStr.isEmpty()) {
            ObservableProperties.setAppViewRequestProperty(new FoldersViewRequest(Paths.get(locationStr)));
        }
    }

    private void setTooltip(Label label) {
        var tooltip = new Tooltip();
        tooltip.setOnShowing(_ -> tooltip.setText(label.getText()));
        label.setTooltip(tooltip);
    }

    private void setOnKeyPressedHandlers() {
        trackListView.setOnKeyPressed(keyEvent -> {
            var keyCode = keyEvent.getCode();
            if (keyEvent.isShortcutDown()) {

            } else {
                if (KeyCode.DELETE == keyCode || KeyCode.F8 == keyCode) {
                    deleteSelectedTracks();
                }
            }
        });
    }

    private void deleteSelectedTracks() {
        if (trackListView.getItems().isEmpty()) {
            LOG.warn("Nothing to delete. The list is empty");
            return;
        }
        var selectedIndices = new ArrayList<>(trackListView.getSelectionModel().getSelectedIndices());

        if (selectedIndices.isEmpty()) {
            LOG.warn("Can't determine selected items to deleted. Selected indices were empty.");
            return;
        }

        // By starting from the highest index and moving toward the lowest, ensure that the positions of the
        // remaining items still need to be removed never change.
        selectedIndices.sort(Collections.reverseOrder());

        LOG.info("Removing tracks at selected indices: {}", selectedIndices);
        selectedIndices.forEach(idx -> {
            var removedTrack = trackListView.getItems().remove(idx.intValue());
            LOG.info("Track at index {} was removed: {} - {}", idx, removedTrack.artistName(), removedTrack.name());
        });
        trackListView.refresh();
        PlayerTrackListManager.remove(selectedIndices);
    }

    private void updateTrackInfoView(Track track) {
        var artistImage = Images.artist(track.id(), 25);
        if (artistImage == null) {
            artistArtImageView.setImage(Images.defaultArtist(25));
        } else {
            artistArtImageView.setImage(artistImage);
            artistImage.errorProperty().addListener((_, _, hasError) -> {
                if (hasError) {
                    var defaultImage = Images.defaultArtist(25);
                    Images.setArtist(track.id(), defaultImage, 25);
                    artistArtImageView.setImage(defaultImage);
                }
            });
        }

        artistNameLabel.setText(track.artistName());
        trackTitleLabel.setText(track.name());

        var albumImage = Images.album(track.id(), 25);
        if (albumImage == null) {
            albumArtImageView.setImage(Images.defaultAlbum(25));
        } else {
            albumArtImageView.setImage(albumImage);
            albumImage.errorProperty().addListener((_, _, hasError) -> {
                if (hasError) {
                    var defaultImage = Images.defaultAlbum(25);
                    Images.setAlbum(track.id(), defaultImage, 25);
                    albumArtImageView.setImage(defaultImage);
                }
            });
        }
        albumNameLabel.setText(track.albumName());
        albumDetailsLabel.setText(track.genre() + ", " + track.releaseDate());

        libraryDetailsLabel.setText(String.format("id: %s, cue: %s, play count: %s, reactions: %s",
                track.id(), track.isCueTrack(), track.playback().count(), track.reaction().name().toLowerCase()));

        audioFormatDetailsLabel.setText(
                track.fileAttributes().extension() + ", " +
                        track.audioFormat().sampleRate() + " kHz, " +
                        track.audioFormat().bitsPerSample() + " bits, " +
                        track.audioFormat().bitRate() + " kbps, " +
                        org.apache.commons.io.FileUtils.byteCountToDisplaySize(track.fileAttributes().size()) + ", " +
                        TimeUtils.durationToTimeFormat(Duration.ofSeconds(track.length().seconds())));

        createdDateLabel.setText(DateUtils.ldFormatted(track.fileAttributes().createdDate().toLocalDate()));
        if (track.fileAttributes().modifiedDate() != null) {
            modifiedDateLabel.setText(DateUtils.ldFormatted(track.fileAttributes().modifiedDate().toLocalDate()));
        } else {
            modifiedDateLabel.setText("n/a");
        }

        var location = track.fileAttributes().location();
        if (track.isCueTrack()) {
            location = MusicLibrary.getTrackById(track.parentId()).fileAttributes().location();
        }
        locationLabel.setText(location);

        var playlists = MusicLibrary.findPlaylistsWithTrackId(track.id());
        playlistsHBox.getChildren().clear();
        if (playlists.isEmpty()) {
            playlistsHBox.getChildren().addFirst(new Label("n/a"));
        } else {
            playlists.stream()
                    .map(c -> {
                        var hl = new Hyperlink(c.name());
                        hl.setOnAction(_ -> ObservableProperties.setAppViewRequestProperty(
                                new PlaylistsViewRequest(c.id(), track.id())));
                        return hl;
                    })
                    .forEach(playlistsHBox.getChildren()::add);
        }

        var collections = MusicLibrary.findCollectionsWithTrackId(track.id());
        collectionsHbox.getChildren().clear();
        if (collections.isEmpty()) {
            collectionsHbox.getChildren().addFirst(new Label("n/a"));
        } else {
            collections.stream()
                    .map(c -> {
                        var hl = new Hyperlink(c.name());
                        hl.setOnAction(_ -> ObservableProperties.setAppViewRequestProperty(
                                new CollectionsViewRequest(c.id(), "" + track.id())));
                        return hl;
                    })
                    .forEach(collectionsHbox.getChildren()::add);
        }
    }

}