package io.playqd.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.data.Track;
import io.playqd.player.FetchMode;
import io.playqd.player.LoopMode;
import io.playqd.player.PlayerEngine;
import io.playqd.utils.PlayqdApis;
import io.playqd.utils.TimeUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private Label sliderTitle, sliderFooterRight, timeElapsedLabel, trackTimeLabel;

    @FXML
    private Button playBtn, playNextBtn, volumeBtn;

    @FXML
    private ToggleButton repeatBtn, shuffleBtn;

    @FXML
    private Slider slider, volumeSlider;

    @FXML
    private void initialize() {
        initPlayerEventConsumers();
        initVolumeChangedListeners();
        initButtonEventHandlers();

        slider.setOnMouseClicked(_ -> {
            PlayerEngine.seek((float) slider.getValue());
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
                        updateHeaderAndFooter(t);
                        updateTrackTime(t);
                        updateControlButtons();
                    });
                }));
        PlayerEngine.eventConsumerRegistry().addStoppedConsumer(() -> Platform.runLater(this::handleTrackStopped));
        PlayerEngine.eventConsumerRegistry().addFinishedConsumer(() -> Platform.runLater(this::handleTrackFinished));
        PlayerEngine.eventConsumerRegistry().addPositionChangedConsumer(newPosition -> slider.setValue(newPosition));
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

    private void updateArtwork(Track track) {
        artworkImageView.setImage(new Image(PlayqdApis.baseUrl() + "/artworks/albums/" + track.album().id()));
    }

    private void updateHeaderAndFooter(Track track) {
        updateSliderTitle(track);
        updateSliderFooter(track);
    }

    private void updateSliderTitle(Track track) {
        sliderTitle.setText(track.artist().name() + " - " + track.title());
    }

    private void updateSliderFooter(Track track) {
        sliderFooterRight.setText(
                track.audioFormat().bitsPerSample() + " bits | " +
                        track.audioFormat().bitRate() + " kbps | " +
                        track.audioFormat().sampleRate() + " kHz, " +
                        track.fileAttributes().extension() + " (" + track.audioFormat().mimeType() + ")");
    }

    private void updateTrackTime(Track track) {
        trackTimeLabel.setText(TimeUtils.durationToTimeFormat(java.time.Duration.ofSeconds(track.length().seconds())));
    }

    private void updateTrackPlayingTime(long newTime) {
        if (newTime > 1000) {
            timeElapsedLabel.setText(TimeUtils.durationToTimeFormat(java.time.Duration.ofMillis(newTime)));
        }
    }

    private void updateControlButtons() {
        playNextBtn.setDisable(!PlayerEngine.PLAYING_QUEUE.hasNext());
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
}
