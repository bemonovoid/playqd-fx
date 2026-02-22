package io.playqd.player;

import io.playqd.controller.music.PlayingQueueListViewCellFactory;
import io.playqd.data.Track;
import io.playqd.event.MouseEventHelper;
import io.playqd.utils.PlayqdApis;
import io.playqd.utils.TimeUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.Duration;
import java.util.ArrayList;

public class PlayerQueueController {

    @FXML
    private ListView<QueuedTrack> queueListView;

    @FXML
    private StackPane artworkStackPane;

    @FXML
    private ImageView artworkImageView;

    @FXML
    private ToolBar trackInfoToolBar;

    @FXML
    private ScrollPane artworkScrollPane;

    @FXML
    private Label tiTrackTitle, tiArtistName, tiAlbumName, tiAudioInfo, tiFilePath;

    @FXML
    private void initialize() {

        trackInfoToolBar.getStyleClass().add("track-info-toolbar");

        artworkStackPane.prefWidthProperty().bind(artworkScrollPane.widthProperty());
        artworkImageView.fitHeightProperty().bind(artworkStackPane.heightProperty());
        artworkImageView.fitWidthProperty().bind(artworkStackPane.widthProperty().subtract(15));
        artworkImageView.setOnMouseClicked(mouseEvent -> {
            if (MouseEventHelper.primaryButtonDoubleClicked(mouseEvent)) {
                showImagePopup(artworkImageView.getImage());
            }
        });

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
        var image = new Image(PlayqdApis.albumArtwork(track.uuid()));
        artworkImageView.setImage(image);

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

    private void showImagePopup(Image image) {
        var selectedTrack = queueListView.getSelectionModel().getSelectedItem().track();

        var popupStage = new Stage();
        popupStage.setTitle(selectedTrack.artistName() + " - "  +selectedTrack.albumName());

        var fullView = new ImageView(image);
        fullView.setPreserveRatio(true);

        var vBox = new VBox(fullView);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(3, 3, 3, 3));
        var root = new Pane(vBox);

//        var scene = new Scene(root, image.getWidth() + 10, image.getHeight() + 10);
        var scene = new Scene(root);

        popupStage.setScene(scene);
        popupStage.setAlwaysOnTop(true);
        popupStage.setResizable(false);
        popupStage.show();
    }
}
