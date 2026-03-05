package io.playqd.player;

import io.playqd.controller.library.PlayingQueueListViewCellFactory;
import io.playqd.data.Track;
import io.playqd.utils.TimeUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToolBar;

import java.time.Duration;
import java.util.ArrayList;

public class PlayerQueueController {

    @FXML
    private ListView<QueuedTrack> queueListView;

    @FXML
    private ToolBar trackInfoToolBar;

    @FXML
    private Label tiTrackTitle, tiArtistName, tiAlbumName, tiAudioInfo, tiFilePath;

    @FXML
    private void initialize() {

        trackInfoToolBar.getStyleClass().add("track-info-toolbar");

        PlayerEngine.PLAYING_QUEUE.queuedTracks().addListener((ListChangeListener<QueuedTrack>) queueChanged -> {
            queueListView.setCellFactory(new PlayingQueueListViewCellFactory());
            queueListView.setItems((FXCollections.observableArrayList(new ArrayList<>(queueChanged.getList()))));
        });
        PlayerEngine.eventConsumerRegistry().addStoppedConsumer(() -> {}); //TODO
        PlayerEngine.eventConsumerRegistry().addFinishedConsumer(() -> {}); //TODO
        PlayerEngine.eventConsumerRegistry().addPlayingConsumer(trackOpt -> {
            trackOpt.ifPresent(track -> {
                Platform.runLater(() -> {
                    queueListView.getItems().stream()
                            .filter(queuedTrack -> queuedTrack.track().id() == track.id())
                            .findFirst()
                            .ifPresent(newPlayingTrack -> {
                                queueListView.getSelectionModel().select(newPlayingTrack);
                                updateTrackInfoView(newPlayingTrack.track());
                            });
                });
            });
        });
    }

    private void updateTrackInfoView(Track track) {
        tiTrackTitle.setText(track.title());
        tiArtistName.setText(track.artistName());
        tiAlbumName.setText(track.albumName());
        tiFilePath.setText(track.fileAttributes().location());
        tiAudioInfo.setText(
                track.fileAttributes().extension() + ", " +
                track.audioFormat().sampleRate() + " kHz, " +
                track.audioFormat().bitsPerSample() + " bits, " +
                track.audioFormat().bitRate() + " kbps, " +
                TimeUtils.durationToTimeFormat(Duration.ofSeconds(track.length().seconds())) + ", " +
                org.apache.commons.io.FileUtils.byteCountToDisplaySize(track.fileAttributes().size()));
    }

}
