package io.playqd.player;

import io.playqd.controller.music.PlayingQueueListViewCellFactory;
import io.playqd.utils.TimeUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.ArrayList;

public class PlayerQueueController {

    @FXML
    private ListView<QueuedTrack> queueListView;

    @FXML
    private void initialize() {
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
                            });
                });
            });
        });
    }
}
