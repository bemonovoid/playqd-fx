package io.playqd.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.data.Track;
import io.playqd.event.MouseEventHelper;
import io.playqd.player.FetchMode;
import io.playqd.player.LoopMode;
import io.playqd.player.Player;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
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
            Player.PLAYING_QUEUE.current()
                    .filter(cueTrack -> cueTrack.cueInfo().parentId() != null)
                    .ifPresentOrElse(cueTrack -> {
                        var parentTrack = MusicLibrary.getTrackById(cueTrack.cueInfo().parentId());
                        var seekCueTime = slider.getValue() * (cueTrack.length().seconds());
                        var seekParentTime = (cueTrack.cueInfo().startTimeInSeconds()) + seekCueTime;
                        var seekPosition = seekParentTime / (parentTrack.length().seconds());
                        Player.seek((float) seekPosition);
                    }, () -> Player.seek((float) slider.getValue()));
        });
    }

    @FXML
    private void play() {
        if (Player.isPlaying()) {
            Player.pause();
        } else {
            Player.resume();
        }
    }

    @FXML
    private void playNext() {
        Player.playNext();
    }

    private void initButtonEventHandlers() {
        favoriteBtn.selectedProperty().addListener((_, _, selected) -> {
            if (selected) {
                Player.PLAYING_QUEUE.current().ifPresent(track -> {
                    MusicLibrary.addToFavorites(track.id());
                    favoriteBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.STAR));
                    favoriteBtn.getStyleClass().add("favorite-icon");
                });
            } else {
                Player.PLAYING_QUEUE.current().ifPresent(track -> {
                    MusicLibrary.removeFromFavorites(track.id());
                    favoriteBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.STAR_ALT));
                    favoriteBtn.getStyleClass().remove("favorite-icon");
                });
            }
        });
        repeatBtn.selectedProperty().addListener((_, _, selected) -> {
            if (selected) {
                repeatBtn.getStyleClass().add("repeat-icon");
                Player.setPlaylistLoopMode(LoopMode.ALL);
            } else {
                repeatBtn.getStyleClass().remove("repeat-icon");
                Player.setPlaylistLoopMode(LoopMode.OFF);
            }
        });
        shuffleBtn.selectedProperty().addListener((_, _, selected) -> {
            if (selected) {
                shuffleBtn.getStyleClass().add("shuffle-icon");
                Player.setPlaylistFetchMode(FetchMode.RANDOM);
            } else {
                shuffleBtn.getStyleClass().remove("shuffle-icon");
                Player.setPlaylistFetchMode(FetchMode.ORDINAL);
            }
        });
    }

    private void initPlayerEventConsumers() {
        Player.onPlayingTrackChanged(track ->
                Platform.runLater(() -> {
                    updatePlayButton(false);
                    updateArtwork(track);
                    updateSliderTitle(track);
                    updateTrackTime(track);
                    updateControlButtons();
                }));
        Player.onStopped((stopped) -> {
            if (stopped) {
                Platform.runLater(this::handleTrackStopped);
            }
        });
        Player.onFinished((track) -> Platform.runLater(() -> handleTrackFinished(track)));
        Player.onPositionChanged(this::updateSliderPosition);
        Player.onPaused(this::updatePlayButton);
        Player.onTimeChanged(newTime -> Platform.runLater(() -> updateTrackPlayingTime(newTime)));
    }

    private void updatePlayButton(boolean paused) {
        if (paused) {
            ((FontAwesomeIconView) playBtn.getGraphic()).setIcon(FontAwesomeIcon.PLAY);
        } else {
            ((FontAwesomeIconView) playBtn.getGraphic()).setIcon(FontAwesomeIcon.PAUSE);
        }
    }

    private void initVolumeChangedListeners() {
        volumeBtn.setOnAction(_ -> {
            if (Player.getVolume() > 0) {
                volumeSlider.setDisable(true);
                Player.setVolume(0);
                ((FontAwesomeIconView) volumeBtn.getGraphic()).setIcon(FontAwesomeIcon.VOLUME_OFF);
            } else {
                volumeSlider.setDisable(false);
                ((FontAwesomeIconView) volumeBtn.getGraphic()).setIcon(FontAwesomeIcon.VOLUME_UP);
                Player.setVolume((int) volumeSlider.getValue());
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
            Player.setVolume(newVolume);
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
        Player.PLAYING_QUEUE.current()
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
            Player.PLAYING_QUEUE.current()
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
        playNextBtn.setDisable(!Player.PLAYING_QUEUE.hasNext());
    }

    private void updateFunctionButtons(Track t) {
        if (t.rating().value() > 0) {
            favoriteBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.STAR));
            favoriteBtn.getStyleClass().add("favorite-icon");
        }
    }

    private void handleTrackFinished(Track track) {
        handleTrackStopped();
        Platform.runLater(() -> {
            MusicLibrary.markAsPlayed(track.id());
        });
        Player.playNext();
    }

    private void handleTrackStopped() {
        slider.setValue(0);
        timeElapsedLabel.setText("0:00");
        ((FontAwesomeIconView) playBtn.getGraphic()).setIcon(FontAwesomeIcon.PLAY);
    }

    private void showFullSizeImageInPopup() {
        Player.PLAYING_QUEUE.current().ifPresent(track -> {
            var screenBounds = Screen.getPrimary().getVisualBounds();
            var maxWidth = screenBounds.getWidth() * 0.7; // 70% of screen width
            var maxHeight = screenBounds.getHeight() * 0.7; // 70% of screen height

            var image = ArtworkImageSetter.getImage(track);

            if (image != null) {

                var imageView = new ImageView(image);

                imageView.setPreserveRatio(true);
                imageView.setFitWidth(maxWidth);
                imageView.setFitHeight(maxHeight);

                var vBox = new VBox(imageView);
                vBox.setAlignment(Pos.CENTER);
                vBox.setPadding(new Insets(10, 10, 10, 10));

                var scene = new Scene(new StackPane(vBox));

                var popupStage = new Stage();

                popupStage.setScene(scene);
                popupStage.setResizable(true);
                popupStage.setAlwaysOnTop(true);
                popupStage.setTitle(track.artistName() + " - " + track.albumName());
                popupStage.show();
            }
        });
    }
}
