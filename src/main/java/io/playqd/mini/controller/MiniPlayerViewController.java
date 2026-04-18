package io.playqd.mini.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.client.Images;
import io.playqd.data.Track;
import io.playqd.event.MouseEventHelper;
import io.playqd.mini.controller.item.AlbumItemRow;
import io.playqd.mini.controller.item.TrackItemRow;
import io.playqd.mini.events.NavigationEvent;
import io.playqd.player.Player;
import io.playqd.service.MusicLibrary;
import io.playqd.utils.ImagePopup;
import io.playqd.utils.TimeUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class MiniPlayerViewController {

    private Consumer<Boolean> onQueueViewToggle;

    @FXML
    private VBox miniPlayerView;

    @FXML
    private ImageView artworkImageView;

    @FXML
    private Hyperlink artistNameLabel, albumNameLabel;

    @FXML
    private Label releaseDateLabel, genreLabel, trackNameLabel, trackLengthLabel, timeElapsedLabel;

    @FXML
    private Slider trackSlider;

    @FXML
    private HBox leftControls, centerControls, rightControls;

    @FXML
    private ToggleButton toggleQueueView;

    @FXML
    private Button playPrevBtn, playBtn, playNextBtn;

    @FXML
    private void initialize() {
        initSlider();
        initPlayerControls();
        initPlayerEventListeners();
    }

    @FXML
    private void onArtworkImageClicked(MouseEvent mouseEvent) {
        if (MouseEventHelper.primaryButtonDoubleClicked(mouseEvent)) {
            if (!artworkImageView.getImage().getUrl().contains("no-album")) {
                Player.playingTrack().ifPresent(ImagePopup::show);
            }
        }
    }

    @FXML
    private void onArtistNameClicked() {
        Player.playingTrack().ifPresent(track -> {
            var navItems = NavigableItemsResolver.resolveArtistAlbums(new TrackItemRow(track));
            miniPlayerView.fireEvent(new NavigationEvent(navItems));
        });
    }

    @FXML
    private void onAlbumNameClicked() {
        Player.playingTrack().ifPresent(track -> {
            var navItems = NavigableItemsResolver.resolveAlbumTracks(new TrackItemRow(track));
            miniPlayerView.fireEvent(new NavigationEvent(navItems));
        });
    }

    private void initSlider() {
        trackSlider.setOnMouseClicked(_ -> {
            Player.playingTrack()
                    .filter(Track::isCueTrack)
                    .ifPresentOrElse(track -> {
                        var parentTrack = MusicLibrary.getTrackById(track.realId());
                        var seekCueTime = trackSlider.getValue() * (track.length().seconds());
                        var seekParentTime = (track.startSecond()) + seekCueTime;
                        var seekPosition = seekParentTime / (parentTrack.length().seconds());
                        Player.seek((float) seekPosition);
                    }, () -> Player.seek((float) trackSlider.getValue()));
        });
        trackSlider.addEventFilter(MouseEvent.MOUSE_DRAGGED, MouseEvent::consume);
        trackSlider.valueProperty().addListener((_, _, newValue) -> {
            var percentage = newValue.doubleValue() * 100;
            var style = String.format("-fx-background-color: linear-gradient(to right, #C92A2AB0 %f%%, #d3d3d3 %f%%);",
                    percentage, percentage
            );
            trackSlider.lookup(".track").setStyle(style);
        });
    }

    private void initPlayerControls() {
        StackPane.setAlignment(leftControls, Pos.CENTER_LEFT);
        StackPane.setAlignment(centerControls, Pos.CENTER);
        StackPane.setAlignment(rightControls, Pos.CENTER_RIGHT);
        initLeftControls();
        initCenterControls();
    }

    private void initLeftControls() {
        toggleQueueView.selectedProperty().addListener((_, _, selected) -> {
            if (selected != null) {
                onQueueViewToggle.accept(selected);
            }
        });
    }

    private void initCenterControls() {


    }

    private void initPlayerEventListeners() {
        Player.onPlayingTrackChanged(track ->
                Platform.runLater(() -> {
                    updateTitle(track);
                    updateArtwork(track);
                    updatePlayButton(false);
                    updateSliderInitialsTimes(track);
                }));
        Player.onStopped((stopped) -> {
            if (stopped) {
                Platform.runLater(this::handleTrackStopped);
            }
        });
        Player.onPositionChanged(this::updateSliderPosition);
        Player.onPaused(this::updatePlayButton);
        Player.onTimeChanged(newTime -> Platform.runLater(() -> updateSliderElapsedTime(newTime)));
    }

    private void updateArtwork(Track track) {
        var size = 85;
        var image = Images.album(track.id(), size);
        if (image == null) {
            artworkImageView.setImage(Images.defaultAlbum(size));
        } else {
            artworkImageView.setImage(image);
            image.errorProperty().addListener((_, _, hasError) -> {
                if (hasError) {
                    var defaultImage = Images.defaultAlbum(size);
                    Images.setAlbum(track.id(), defaultImage, size);
                    artworkImageView.setImage(defaultImage);
                }
            });
        }
    }

    private void updateTitle(Track track) {
        artistNameLabel.setText("by " + track.artistName());
        trackNameLabel.setText(track.name());
        albumNameLabel.setText(track.albumName());
        releaseDateLabel.setText(track.releaseDate());
        genreLabel.setText(track.genre());
    }

    private void updateSliderInitialsTimes(Track track) {
        timeElapsedLabel.setText("0:00");
        trackLengthLabel.setText(TimeUtils.durationToTimeFormat(java.time.Duration.ofSeconds(track.length().seconds())));
    }

    private void updateSliderElapsedTime(long newTime) {
        if (newTime > 1000) {
            Player.playingTrack()
                    .filter(Track::isCueTrack)
                    .ifPresentOrElse(track -> {
                        // the 'newTime' is relative to parent track
                        var newCueTimeInMillis = newTime - (((long) track.startSecond()) * 1000);
                        var progress = (double) newCueTimeInMillis / (track.length().seconds() * 1000);
                        trackSlider.setValue(progress);
                        timeElapsedLabel.setText(
                                TimeUtils.durationToTimeFormat(java.time.Duration.ofMillis(newCueTimeInMillis)));
                    }, () -> timeElapsedLabel.setText(
                            TimeUtils.durationToTimeFormat(java.time.Duration.ofMillis(newTime))));
        }
    }

    private void updateSliderPosition(double newValue) {
        Player.playingTrack()
                // cue tracks are supposed to have parent id.
                // slider position for cue tracks is updated upon time changed callback.
                .filter(track -> track.parentId() == null)
                .ifPresent(_ -> trackSlider.setValue(newValue));
    }

    private void updatePlayButton(boolean paused) {
        if (paused) {
            ((FontAwesomeIconView) playBtn.getGraphic()).setIcon(FontAwesomeIcon.PLAY);
        } else {
            ((FontAwesomeIconView) playBtn.getGraphic()).setIcon(FontAwesomeIcon.PAUSE);
        }
    }

    private void handleTrackStopped() {
        trackSlider.setValue(0);
        timeElapsedLabel.setText("0:00");
        ((FontAwesomeIconView) playBtn.getGraphic()).setIcon(FontAwesomeIcon.PLAY);
    }

    void setOnQueueViewToggle(Consumer<Boolean> onQueueViewToggle) {
        this.onQueueViewToggle = onQueueViewToggle;
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
        var hasNext = Player.playNext();
        playNextBtn.setDisable(!hasNext);
    }

    @FXML
    private void playPrevious() {
        var hasNext = Player.playPrevious();
        playPrevBtn.setDisable(!hasNext);
    }
}
