package io.playqd.player;

import io.playqd.data.Track;
import io.playqd.utils.TimeUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerTrackListViewController extends PlayerTrackListView {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerTrackListViewController.class);

    @FXML
    private ListView<Track> trackListView;

    @FXML
    private Label trackNameLabel, artistNameLabel, albumNameLabel, albumInfoLabel, fileInfoLabel, fileLocationLabel;

    @Override
    void setItems(List<Track> tracks) {;
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
        PlayerTrackListManager.setPlayerTrackListView(this);
        initTooltips();
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

    private void initTooltips() {
        setTooltip(trackNameLabel);
        setTooltip(artistNameLabel);
        setTooltip(albumNameLabel);
        setTooltip(fileLocationLabel);
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

    private void updateTrackInfoView(Track track) {
        trackNameLabel.setText(track.title());
        artistNameLabel.setText(track.artistName());
        albumNameLabel.setText(track.albumName());
        albumInfoLabel.setText(track.genre() + ", " + track.releaseDate());
        fileInfoLabel.setText(
                track.fileAttributes().extension() + ", " +
                        track.audioFormat().sampleRate() + " kHz, " +
                        track.audioFormat().bitsPerSample() + " bits, " +
                        track.audioFormat().bitRate() + " kbps, " +
                        org.apache.commons.io.FileUtils.byteCountToDisplaySize(track.fileAttributes().size()) + ", " +
                        TimeUtils.durationToTimeFormat(Duration.ofSeconds(track.length().seconds())));
        fileLocationLabel.setText(track.fileAttributes().location());
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
            LOG.info("Track at index {} was removed: {} - {}", idx, removedTrack.artistName(), removedTrack.title());
        });
        trackListView.refresh();
        PlayerTrackListManager.remove(selectedIndices);
    }

}