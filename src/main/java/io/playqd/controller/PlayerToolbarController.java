package io.playqd.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.data.Track;
import io.playqd.event.MouseEventHelper;
import io.playqd.player.FetchMode;
import io.playqd.player.LoopMode;
import io.playqd.player.PlayerEngine;
import io.playqd.service.MusicLibrary;
import io.playqd.utils.ArtworkImageSetter;
import io.playqd.utils.TimeUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * //TODO disable slider while on pause
 */
public class PlayerToolbarController {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerToolbarController.class);

    private static final int DOWN_VOLUME = 30;

    @FXML
    private ImageView artworkImageView;

    @FXML
    private Label sliderTitle, timeElapsedLabel, trackTimeLabel;

    @FXML
    private Button playBtn, playNextBtn, volumeBtn;

    @FXML
    private ToggleButton favoriteBtn, repeatBtn, shuffleBtn;

    @FXML
    private Slider slider, volumeSlider;

    @FXML
    private void initialize() {
        initArtworkImageListeners();
        initPlayerEventConsumers();
        initVolumeChangedListeners();
        initButtonEventHandlers();

        slider.setOnMouseClicked(_ -> {
            PlayerEngine.PLAYING_QUEUE.current()
                    .filter(cueTrack -> cueTrack.cueInfo().parentId() != null)
                    .ifPresentOrElse(cueTrack -> {
                        var parentTrack = MusicLibrary.getTrackById(cueTrack.cueInfo().parentId());
                        var seekCueTime = slider.getValue() * (cueTrack.length().seconds() * 1000);
                        var seekParentTime = cueTrack.cueInfo().startTimeInSeconds() + seekCueTime;
                        var seekPosition = seekParentTime / (parentTrack.length().seconds() * 1000);
                        PlayerEngine.seek((float) seekPosition);
                    }, () -> PlayerEngine.seek((float) slider.getValue()));
        });
    }

    @FXML
    private void play() {
        if (PlayerEngine.isPlaying()) {
            PlayerEngine.pause();
        } else {
            PlayerEngine.resume();
        }
    }

    @FXML
    private void playNext() {
        PlayerEngine.playNext();
    }

    private void initButtonEventHandlers() {
        favoriteBtn.selectedProperty().addListener((_, _, selected) -> {
            if (selected) {
                PlayerEngine.PLAYING_QUEUE.current().ifPresent(track -> {
                    MusicLibrary.addToFavorites(track.id());
                    favoriteBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.STAR));
                    favoriteBtn.getStyleClass().add("favorite-icon");
                });
            } else {
                PlayerEngine.PLAYING_QUEUE.current().ifPresent(track -> {
                    MusicLibrary.removeFromFavorites(track.id());
                    favoriteBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.STAR_ALT));
                    favoriteBtn.getStyleClass().remove("favorite-icon");
                });
            }
        });
        repeatBtn.selectedProperty().addListener((_, _, selected) -> {
            if (selected) {
                repeatBtn.getStyleClass().add("repeat-icon");
                PlayerEngine.setPlaylistLoopMode(LoopMode.ALL);
            } else {
                repeatBtn.getStyleClass().remove("repeat-icon");
                PlayerEngine.setPlaylistLoopMode(LoopMode.NONE);
            }
        });
        shuffleBtn.selectedProperty().addListener((_, _, selected) -> {
            if (selected) {
                shuffleBtn.getStyleClass().add("shuffle-icon");
                PlayerEngine.setPlaylistFetchMode(FetchMode.RANDOM);
            } else {
                shuffleBtn.getStyleClass().remove("shuffle-icon");
                PlayerEngine.setPlaylistFetchMode(FetchMode.ORDINAL);
            }
        });
    }

    private void initPlayerEventConsumers() {
        PlayerEngine.eventConsumerRegistry().addPlayingConsumer(track ->
                Platform.runLater(() -> {
                    ((FontAwesomeIconView) playBtn.getGraphic()).setIcon(FontAwesomeIcon.PAUSE);
                    track.ifPresent(t -> {
                        updateArtwork(t);
                        updateSliderTitle(t);
                        updateTrackTime(t);
                        updateControlButtons();
                    });
                }));
        PlayerEngine.eventConsumerRegistry().addStoppedConsumer(
                () -> Platform.runLater(this::handleTrackStopped));
        PlayerEngine.eventConsumerRegistry().addFinishedConsumer(
                () -> Platform.runLater(this::handleTrackFinished));
        PlayerEngine.eventConsumerRegistry().addPositionChangedConsumer(
                this::updateSliderPosition);
        PlayerEngine.eventConsumerRegistry()
                .addPausedConsumer(() -> ((FontAwesomeIconView) playBtn.getGraphic()).setIcon(FontAwesomeIcon.PLAY));
        PlayerEngine.eventConsumerRegistry()
                .addTimeChangedConsumer(newTime -> Platform.runLater(() -> updateTrackPlayingTime(newTime)));
    }

    private void initVolumeChangedListeners() {
        volumeBtn.setOnAction(_ -> {
            if (PlayerEngine.getVolume() > 0) {
                volumeSlider.setDisable(true);
                PlayerEngine.setVolume(0);
                ((FontAwesomeIconView) volumeBtn.getGraphic()).setIcon(FontAwesomeIcon.VOLUME_OFF);
            } else {
                volumeSlider.setDisable(false);
                ((FontAwesomeIconView) volumeBtn.getGraphic()).setIcon(FontAwesomeIcon.VOLUME_UP);
                PlayerEngine.setVolume((int) volumeSlider.getValue());
            }
        });

        var volumeTooltip = new Tooltip();
        volumeTooltip.setShowDelay(Duration.millis(100));
        volumeSlider.setTooltip(volumeTooltip);

        volumeSlider.valueProperty().addListener((_, oldValue, newValue) -> {
            var oldVolume = oldValue.intValue();
            var newVolume = newValue.intValue();
            if (oldVolume == 0) {
                if (newVolume <= DOWN_VOLUME) {
                    ((FontAwesomeIconView) volumeBtn.getGraphic()).setIcon(FontAwesomeIcon.VOLUME_DOWN);
                } else {
                    ((FontAwesomeIconView) volumeBtn.getGraphic()).setIcon(FontAwesomeIcon.VOLUME_UP);
                }
            } else if (newVolume == 0) {
                ((FontAwesomeIconView) volumeBtn.getGraphic()).setIcon(FontAwesomeIcon.VOLUME_OFF);
            } else if (newVolume <= DOWN_VOLUME && oldVolume > DOWN_VOLUME) {
                ((FontAwesomeIconView) volumeBtn.getGraphic()).setIcon(FontAwesomeIcon.VOLUME_DOWN);
            } else if (newVolume > DOWN_VOLUME && oldVolume <= DOWN_VOLUME) {
                ((FontAwesomeIconView) volumeBtn.getGraphic()).setIcon(FontAwesomeIcon.VOLUME_UP);
            }
            volumeTooltip.setText(newVolume + "%");
            PlayerEngine.setVolume(newVolume);
        });
    }

    private void initArtworkImageListeners() {
        artworkImageView.setOnMouseClicked(mouseEvent -> {
            if (MouseEventHelper.primaryButtonDoubleClicked(mouseEvent)) {
                if (ArtworkImageSetter.isNotFoundImageUrl(artworkImageView.getImage().getUrl())) {
                    return;
                }
                showFullSizeImageInPopup();
            }
        });
    }

    private void updateArtwork(Track track) {
        ArtworkImageSetter.set(track, 80, artworkImageView);
    }

    private void updateSliderPosition(double newValue) {
        PlayerEngine.PLAYING_QUEUE.current()
                .filter(track -> track.cueInfo().parentId() == null)
                .ifPresent(_ -> slider.setValue(newValue));
    }

    private void updateSliderTitle(Track track) {
        sliderTitle.setText(track.artistName() + " - " + track.title());
    }

    private void updateTrackTime(Track track) {
        trackTimeLabel.setText(TimeUtils.durationToTimeFormat(java.time.Duration.ofSeconds(track.length().seconds())));
    }

    private void updateTrackPlayingTime(long newTime) {
        if (newTime > 1000) {
            PlayerEngine.PLAYING_QUEUE.current()
                    .filter(track -> track.cueInfo().parentId() != null)
                    .ifPresentOrElse(track -> {
                        // the 'newTime' is relative to parent track
                        var newCueTimeInMillis = newTime - (((long) track.cueInfo().startTimeInSeconds()) * 1000);
                        var progress = (double) newCueTimeInMillis / (track.length().seconds() * 1000);
                        slider.setValue(progress);
                        timeElapsedLabel.setText(
                                TimeUtils.durationToTimeFormat(java.time.Duration.ofMillis(newCueTimeInMillis)));
                    }, () -> timeElapsedLabel.setText(
                            TimeUtils.durationToTimeFormat(java.time.Duration.ofMillis(newTime))));
        }
    }

    private void updateControlButtons() {
        playNextBtn.setDisable(!PlayerEngine.PLAYING_QUEUE.hasNext());
    }

    private void updateFunctionButtons(Track t) {
        if (t.rating().value() > 0) {
            favoriteBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.STAR));
            favoriteBtn.getStyleClass().add("favorite-icon");
        }
    }

    private void handleTrackStopped() {
        slider.setValue(0);
        timeElapsedLabel.setText("0:00");
        ((FontAwesomeIconView) playBtn.getGraphic()).setIcon(FontAwesomeIcon.PLAY);
    }

    private void handleTrackFinished() {
        handleTrackStopped();
        PlayerEngine.playNext();
    }

    private void showFullSizeImageInPopup() {
        PlayerEngine.PLAYING_QUEUE.current().ifPresent(track -> {
            var popupStage = new Stage();
            popupStage.setTitle(track.artistName() + " - "  + track.albumName());

            var window = artworkImageView.getScene().getWindow();
            var size = window.getHeight() - 300;

            var imageView = new ImageView();
            imageView.setSmooth(true);
            imageView.setPreserveRatio(true);
            ArtworkImageSetter.set(track, (int) size, imageView);

            var vBox = new VBox(imageView);
            vBox.setAlignment(Pos.CENTER);

            vBox.setPadding(new Insets(10, 10, 10, 10));


            var scene = new Scene(vBox);

            popupStage.setScene(scene);
            popupStage.setAlwaysOnTop(true);
            popupStage.setResizable(false);
            popupStage.show();
        });
    }
}
