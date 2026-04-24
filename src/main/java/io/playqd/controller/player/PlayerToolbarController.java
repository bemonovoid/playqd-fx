package io.playqd.controller.player;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.client.Images;
import io.playqd.config.AppConfig;
import io.playqd.controller.view.ObservableProperties;
import io.playqd.controller.view.menuitem.CollectionsMenuItems;
import io.playqd.controller.view.request.MusicLibraryViewRequest;
import io.playqd.data.NewMediaCollectionItem;
import io.playqd.data.Reaction;
import io.playqd.data.Track;
import io.playqd.event.MouseEventHelper;
import io.playqd.player.FetchMode;
import io.playqd.player.LoopMode;
import io.playqd.player.Player;
import io.playqd.player.PlayerTrack;
import io.playqd.service.MusicLibrary;
import io.playqd.utils.ImagePopup;
import io.playqd.utils.TimeUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.controlsfx.control.HyperlinkLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class PlayerToolbarController {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerToolbarController.class);

    private static final int DEFAULT_VOLUME = 50;
    private static final int DOWN_VOLUME = 30;

    @FXML
    private ImageView artworkImageView;

    @FXML
    private HyperlinkLabel artistNameLinkLabel;

    @FXML
    private Label trackTitleLabel, timeElapsedLabel, trackTimeLabel;

    @FXML
    private Button playPrevBtn, playBtn, playNextBtn, volumeBtn;

    @FXML
    private Button thumbsUpBtn, thumbsDownBtn;

    @FXML
    private ToggleButton repeatBtn, shuffleBtn;

    @FXML
    private Slider slider, volumeSlider;

    @FXML
    private void initialize() {
        initArtworkImageListeners();
        initTitleListeners();
        initPlayerEventConsumers();
        initVolumeControl();
        initButtonEventHandlers();

        slider.setOnMouseClicked(_ -> {
            Player.playerTrack()
                    .map(PlayerTrack::track)
                    .filter(Track::isCueTrack)
                    .ifPresentOrElse(track -> {
                        var parentTrack = MusicLibrary.getTrackById(track.realId());
                        var seekCueTime = slider.getValue() * (track.length().seconds());
                        var seekParentTime = (track.startSecond()) + seekCueTime;
                        var seekPosition = seekParentTime / (parentTrack.length().seconds());
                        Player.seek((float) seekPosition);
                    }, () -> Player.seek((float) slider.getValue()));
        });
        slider.addEventFilter(MouseEvent.MOUSE_DRAGGED, MouseEvent::consume);
        slider.getStyleClass().add("progression-slider");
        slider.valueProperty().addListener((_, _, newValue) -> {
            var percentage = newValue.doubleValue() * 100;
            var style = String.format("-fx-background-color: linear-gradient(to right, #C92A2AB0 %f%%, #d3d3d3 %f%%);",
                    percentage, percentage
            );
            slider.lookup(".track").setStyle(style);
        });
    }

    @FXML
    private void playPrevious() {
        var hasNext = Player.playPrevious();
        playPrevBtn.setDisable(!hasNext);
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

    private void initButtonEventHandlers() {
        thumbsUpBtn.setOnAction(_ -> {
            removeThumbsUpReactionBtnStyle();
            removeThumbsDownReactionBtnStyle();
            Player.playerTrack().map(PlayerTrack::track).ifPresent(track -> {
                var trackFromLibrary = MusicLibrary.getTrackById(track.id());
                var newReaction = Reaction.THUMB_UP == trackFromLibrary.reaction() ? Reaction.NONE : Reaction.THUMB_UP;
//                MusicLibrary.updateReaction(newReaction, List.of(track.id()));
                if (Reaction.NONE != newReaction) {
                    setThumbsUpReactionBtnStyle();
                }
            });
        });
        thumbsDownBtn.setOnAction(_ -> {
            removeThumbsUpReactionBtnStyle();
            removeThumbsDownReactionBtnStyle();
            Player.playerTrack().map(PlayerTrack::track).ifPresent(track -> {
                var trackFromLibrary = MusicLibrary.getTrackById(track.id());
                var newReaction = Reaction.THUMB_DOWN == trackFromLibrary.reaction() ? Reaction.NONE : Reaction.THUMB_DOWN;
//                MusicLibrary.updateReaction(newReaction, List.of(track.id()));
                if (Reaction.NONE != newReaction) {
                    setThumbsDownReactionBtnStyle();
                }
            });
        });
        shuffleBtn.selectedProperty().addListener((_, _, selected) -> {
            if (selected) {
                Player.fetchMode(FetchMode.RANDOM);
                var icon = new FontAwesomeIconView(FontAwesomeIcon.RANDOM);
                icon.setStyle("-fx-fill: #00d000");
                shuffleBtn.setGraphic(icon);
            } else {
                Player.fetchMode(FetchMode.NORMAL);
                shuffleBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.RANDOM));
            }
        });
        repeatBtn.selectedProperty().addListener((_, _, selected) -> {
            if (selected) {
                Player.loopMode(LoopMode.ON);
                var icon = new FontAwesomeIconView(FontAwesomeIcon.REPEAT);
                icon.setStyle("-fx-fill: #00d000");
                repeatBtn.setGraphic(icon);
            } else {
                Player.loopMode(LoopMode.OFF);
                repeatBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.REPEAT));
            }
        });
    }

    private void initPlayerEventConsumers() {
        Player.onPlayingTrackChanged(track ->
                Platform.runLater(() -> {
                    updateArtwork(track);
                    updatePlayButton(false);
                    updateSliderTitle(track);
                    updateTrackTime(track);
                    updateReactionButtonOnTrackChanged(track);
                }));
        Player.onStopped((stopped) -> {
            if (stopped) {
                Platform.runLater(this::handleTrackStopped);
            }
        });
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

    private void initVolumeControl() {
        LOG.info("Initializing 'audio volume' control ...");

        var volumeTooltip = new Tooltip();
        volumeTooltip.setShowDelay(Duration.millis(100));
        volumeSlider.setTooltip(volumeTooltip);

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
            LOG.info("Slider volume = {}", newValue);
            LOG.info("Player volume = {}", Player.getVolume());
        });

        var volumeState = AppConfig.getProperties().player().state().volume();
        var volume = volumeState != null ? volumeState.get() : DEFAULT_VOLUME;
        volumeSlider.setValue(volume);
        AppConfig.getProperties().player().state().volume().bind(volumeSlider.valueProperty());

        LOG.info("'audio volume' control initialization completed. Volume = {}", volume);
    }

    private void initArtworkImageListeners() {
        artworkImageView.setOnMouseClicked(mouseEvent -> {
            if (Images.isDefaultImage(artworkImageView.getImage().getUrl())) {
                return;
            }
            if (MouseEventHelper.primaryButtonDoubleClicked(mouseEvent)) {
                showFullSizeImageInPopup();
            } else if (MouseEventHelper.secondaryButtonSingleClicked(mouseEvent)) {
                var collectionsMenuItems = new CollectionsMenuItems()
                        .onAddItemsToCollection(() -> Player.playerTrack().map(PlayerTrack::track)
                                .map(track -> List.of(NewMediaCollectionItem.createForAlbumArtwork(track)))
                                .orElse(Collections.emptyList()))
                        .build();
                var contextMenu = new ContextMenu();
                contextMenu.getItems().addAll(collectionsMenuItems);
                contextMenu.show(artworkImageView, mouseEvent.getScreenX(), mouseEvent.getScreenY());
            }
        });
    }

    private void initTitleListeners() {
        artistNameLinkLabel.setOnAction(e -> {
            var track = (Track) artistNameLinkLabel.getUserData();
            if (track != null) {
                ObservableProperties.setAppViewRequestProperty(new MusicLibraryViewRequest(track));
            }
            e.consume();
        });
    }

    private void updateArtwork(Track track) {
        var size = 80;
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

    private void updateSliderPosition(double newValue) {
        Player.playerTrack()
                // cue tracks are supposed to have parent id.
                // slider position for cue tracks is updated upon time changed callback.
                .map(PlayerTrack::track)
                .filter(track -> track.parentId() == null)
                .ifPresent(_ -> slider.setValue(newValue));
    }

    private void updateSliderTitle(Track track) {
        artistNameLinkLabel.setUserData(track);
        artistNameLinkLabel.setText("[" + track.artistName() + "]");
        trackTitleLabel.setText(" - " + track.name());
    }

    private void updateTrackTime(Track track) {
        trackTimeLabel.setText(TimeUtils.durationToTimeFormat(java.time.Duration.ofSeconds(track.length().seconds())));
    }

    private void updateReactionButtonOnTrackChanged(Track track) {
        removeThumbsUpReactionBtnStyle();
        removeThumbsDownReactionBtnStyle();
        if (Reaction.THUMB_UP == track.reaction()) {
            setThumbsUpReactionBtnStyle();
        } else if (Reaction.THUMB_DOWN == track.reaction()) {
            setThumbsDownReactionBtnStyle();
        }
    }

    private void updateTrackPlayingTime(long newTime) {
        if (newTime > 1000) {
            Player.playerTrack()
                    .map(PlayerTrack::track)
                    .filter(Track::isCueTrack)
                    .ifPresentOrElse(track -> {
                        // the 'newTime' is relative to parent track
                        var newCueTimeInMillis = newTime - (((long) track.startSecond()) * 1000);
                        var progress = (double) newCueTimeInMillis / (track.length().seconds() * 1000);
                        slider.setValue(progress);
                        timeElapsedLabel.setText(
                                TimeUtils.durationToTimeFormat(java.time.Duration.ofMillis(newCueTimeInMillis)));
                    }, () -> timeElapsedLabel.setText(
                            TimeUtils.durationToTimeFormat(java.time.Duration.ofMillis(newTime))));
        }
    }

    private void handleTrackStopped() {
        slider.setValue(0);
        timeElapsedLabel.setText("0:00");
        ((FontAwesomeIconView) playBtn.getGraphic()).setIcon(FontAwesomeIcon.PLAY);
    }

    private void showFullSizeImageInPopup() {
        Player.playerTrack().map(PlayerTrack::track).ifPresent(ImagePopup::show);
    }

    private void setThumbsUpReactionBtnStyle() {
        var icon = (FontAwesomeIconView) thumbsUpBtn.getGraphic();
        icon.setStyle("-fx-fill: #00d000");
    }

    private void setThumbsDownReactionBtnStyle() {
        var icon = (FontAwesomeIconView) thumbsDownBtn.getGraphic();
        icon.setStyle("-fx-fill: #ff6703");
    }

    private void removeThumbsUpReactionBtnStyle() {
        removeThumbsUpReactionBtnStyle((FontAwesomeIconView) thumbsUpBtn.getGraphic());
    }

    private void removeThumbsDownReactionBtnStyle() {
        removeThumbsDownReactionBtnStyle((FontAwesomeIconView) thumbsDownBtn.getGraphic());
    }

    private void removeThumbsUpReactionBtnStyle(FontAwesomeIconView icon) {
        icon.setStyle(icon.getStyle().replace("-fx-fill: #00d000", ""));
    }

    private void removeThumbsDownReactionBtnStyle(FontAwesomeIconView icon) {
        icon.setStyle(icon.getStyle().replace("-fx-fill: #ff6703", ""));
    }
}
