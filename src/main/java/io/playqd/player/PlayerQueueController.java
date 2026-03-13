package io.playqd.player;

import io.playqd.data.Track;
import io.playqd.utils.TimeUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;

import java.time.Duration;
import java.util.ArrayList;

public class PlayerQueueController {

    @FXML
    private ListView<QueuedTrack> queueListView;

    @FXML
    private Label trackNameLabel, artistNameLabel, albumNameLabel, albumInfoLabel, fileInfoLabel, fileLocationLabel;

    @FXML
    private void initialize() {
        initTooltips();
        Player.PLAYING_QUEUE.queuedTracks().addListener((ListChangeListener<QueuedTrack>) queueChanged -> {
            queueListView.setCellFactory(new PlayerQueueListViewCellFactory());
            queueListView.setItems((FXCollections.observableArrayList(new ArrayList<>(queueChanged.getList()))));
        });
        Player.onPlayingTrackChanged(track ->
                Platform.runLater(() ->
                        queueListView.getItems().stream()
                                .filter(queuedTrack -> queuedTrack.track().id() == track.id())
                                .findFirst()
                                .ifPresent(newPlayingTrack -> {
                                    queueListView.getSelectionModel().select(newPlayingTrack);
                                    updateTrackInfoView(newPlayingTrack.track());
                                })));
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

}