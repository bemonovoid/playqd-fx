package io.playqd.mini.controller;

import java.util.HashSet;
import java.util.List;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome6.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.playqd.client.Images;
import io.playqd.config.AppConfig;
import io.playqd.data.Reaction;
import io.playqd.data.Track;
import io.playqd.event.MouseEventHelper;
import io.playqd.mini.controller.item.TrackItemRow;
import io.playqd.mini.events.NavigationEvent;
import io.playqd.player.FetchMode;
import io.playqd.player.PlaybackMode;
import io.playqd.player.Player;
import io.playqd.player.PlayerTrack;
import io.playqd.player.TrackListRequest;
import io.playqd.service.MusicLibrary;
import io.playqd.utils.ImagePopup;
import io.playqd.utils.TimeUtils;

public class MiniPlayerViewController {

    private static final Logger LOG = LoggerFactory.getLogger(MiniPlayerViewController.class);

    private double unMuteVolume = -1;

    @FXML
    private VBox miniPlayerView;

    @FXML
    private ImageView artworkImageView;

    @FXML
    private Hyperlink artistNameLabel, albumNameLabel;

    @FXML
    private Label releaseDateLabel, genreLabel, trackNameLabel, trackLengthLabel, timeElapsedLabel;

    @FXML
    private Slider trackSlider, volumeSlider;

    @FXML
    private HBox leftControls, centerControls, rightControls;

    @FXML
    private MenuButton quickNavItemsMenuBtn;

    @FXML
    private Button reactionBtn, playPrevBtn, playBtn, playNextBtn, playbackModeBtn, volumeBtn;

    @FXML
    private ToggleButton fetchModeToggleBtn;

    @FXML
    private void initialize() {
        initPlayerState();
        initSlider();
        initVolumeSlider();
        initPlayerControls();
        initPlayerEventListeners();
        initLibraryEventListeners();
    }

    @FXML
    private void onArtworkImageClicked(MouseEvent mouseEvent) {
        if (MouseEventHelper.primaryButtonDoubleClicked(mouseEvent)) {
            if (!artworkImageView.getImage().getUrl().contains("no-album")) {
                Player.playerTrack().map(PlayerTrack::track).ifPresent(ImagePopup::show);
            }
        }
    }

    @FXML
    private void onArtistNameClicked() {
        Player.playerTrack().map(PlayerTrack::track).ifPresent(track -> {
            var navItems = NavigableItemsResolver.resolveArtistAlbums(new TrackItemRow(track));
            miniPlayerView.fireEvent(new NavigationEvent(navItems));
        });
    }

    @FXML
    private void onAlbumNameClicked() {
        Player.playerTrack().map(PlayerTrack::track).ifPresent(track -> {
            var navItems = NavigableItemsResolver.resolveAlbumTracks(new TrackItemRow(track));
            miniPlayerView.fireEvent(new NavigationEvent(navItems));
        });
    }

    @FXML
    private void changeReaction() {
        Player.playerTrack().map(PlayerTrack::track).ifPresent(track -> {
            var reaction = Reaction.THUMB_UP == track.reaction() ? Reaction.NONE : Reaction.THUMB_UP;
            MusicLibrary.updateReaction(List.of(track.id()), reaction);
        });
    }

    private void initPlayerState() {
        var lastPlayedTrackIdProp = AppConfig.getProperties().player().state().lastPlayedTrackId();
        var lastPlayingQueue = new HashSet<>(AppConfig.getProperties().player().state().tracklist());
        if (lastPlayedTrackIdProp != null && lastPlayedTrackIdProp.get() > 0) {
            var playerTrack = MusicLibrary.getTrackById(lastPlayedTrackIdProp.get());
            updateArtwork(playerTrack);
            updateTitle(playerTrack);
            var tracks = MusicLibrary.getTracksById(lastPlayingQueue);
            var startIdx = 0;
            for (int i = 0; i < tracks.size(); i++) {
                if (tracks.get(i).id() == playerTrack.id()) {
                    startIdx = i;
                    break;
                }
            }
            Player.enqueue(new TrackListRequest(startIdx, tracks, false));
        }
    }

    private void initSlider() {
        trackSlider.setOnMouseClicked(_ -> Player.playerTrack()
                .map(PlayerTrack::track)
                .filter(Track::isCueTrack)
                .ifPresentOrElse(track -> {
                    var parentTrack = MusicLibrary.getTrackById(track.realId());
                    var seekCueTime = trackSlider.getValue() * (track.length().seconds());
                    var seekParentTime = (track.startSecond()) + seekCueTime;
                    var seekPosition = seekParentTime / (parentTrack.length().seconds());
                    Player.seek((float) seekPosition);
                }, () -> Player.seek((float) trackSlider.getValue())));
        trackSlider.addEventFilter(MouseEvent.MOUSE_DRAGGED, MouseEvent::consume);
        trackSlider.valueProperty().addListener((_, _, newValue) -> {
            var percentage = newValue.doubleValue() * 100;
            var style = String.format("-fx-background-color: linear-gradient(to right, #C92A2AB0 %f%%, #d3d3d3 %f%%);",
                    percentage, percentage
            );
            trackSlider.lookup(".track").setStyle(style);
        });
    }

    private void initVolumeSlider() {
        var savedVolumeProperty = AppConfig.getProperties().player().state().volume();
        var savedVolume = savedVolumeProperty.get();

        var tooltip = new Tooltip();
        tooltip.setShowDelay(Duration.millis(100));

        volumeSlider.setTooltip(tooltip);
        volumeSlider.valueProperty().addListener((_, _, newValue) -> {
            var newVolume = newValue.intValue();
            var lowVolumeLevel = 30;
            if (newVolume <= 0) {
                volumeBtn.setGraphic(FontIcon.of(FontAwesomeSolid.VOLUME_MUTE, 12));
            } else if (newVolume <= lowVolumeLevel) {
                volumeBtn.setGraphic(FontIcon.of(FontAwesomeSolid.VOLUME_DOWN, 12));
            } else {
                volumeBtn.setGraphic(FontIcon.of(FontAwesomeSolid.VOLUME_UP, 12));
            }
            tooltip.setText("" + newVolume);
            Player.setVolume(newVolume);
        });
        volumeSlider.setValue(savedVolume);
        Player.setVolume((int) savedVolume);
        savedVolumeProperty.bind(volumeSlider.valueProperty());
    }

    private void initPlayerControls() {
        StackPane.setAlignment(leftControls, Pos.CENTER_LEFT);
        StackPane.setAlignment(centerControls, Pos.CENTER);
        StackPane.setAlignment(rightControls, Pos.CENTER_RIGHT);
        initLeftControls();
        initCenterControls();
    }

    private void initLeftControls() {
        quickNavItemsMenuBtn.getStyleClass().setAll("button", "icon-button");
        quickNavItemsMenuBtn.getItems().addAll(QuickNavigationMenuItems.get(quickNavItemsMenuBtn));
    }

    private void initCenterControls() {
        updateFetchModeButton(Player.getFetchMode());
        updatePlaybackModeButton(Player.getPlaybackMode());
        fetchModeToggleBtn.selectedProperty().addListener((_, _, selected) ->
                Player.setFetchMode(selected ? FetchMode.RANDOM : FetchMode.NORMAL));
    }

    private void initPlayerEventListeners() {
        Player.onPlayingTrackChanged(track ->
                Platform.runLater(() -> {
                    updateTitle(track);
                    updateArtwork(track);
                    updateReactionButton(track);
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
        Player.onPlaybackModeChanged(this::updatePlaybackModeButton);
    }

    private void initLibraryEventListeners() {
        MusicLibrary.updatedTracksProperty().addListener((_, _, newValue) -> {
            if (newValue != null && newValue.tracks() != null && newValue.tracks().size() == 1) {
                Platform.runLater(() -> {
                    var track = newValue.tracks().getFirst();
                    updateReactionButton(track);
                });
            }
        });
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
            Player.playerTrack()
                    .map(PlayerTrack::track)
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
        Player.playerTrack()
                // cue tracks are supposed to have parent id.
                // slider position for cue tracks is updated upon time changed callback.
                .map(PlayerTrack::track)
                .filter(track -> track.parentId() == null)
                .ifPresent(_ -> trackSlider.setValue(newValue));
    }

    private void updateReactionButton(Track track) {
        var icon = (FontIcon) null;
        if (Reaction.THUMB_UP == track.reaction()) {
            icon = FontIcon.of(FontAwesomeSolid.HEART, Color.web("#ff6688"));
        } else {
            icon = FontIcon.of(FontAwesomeRegular.HEART);
        }
        reactionBtn.setGraphic(icon);
    }

    private void updatePlayButton(boolean paused) {
        if (paused) {
            ((FontAwesomeIconView) playBtn.getGraphic()).setIcon(FontAwesomeIcon.PLAY);
        } else {
            ((FontAwesomeIconView) playBtn.getGraphic()).setIcon(FontAwesomeIcon.PAUSE);
        }
    }

    private void updatePlaybackModeButton(PlaybackMode playbackMode) {
        switch (playbackMode) {
            case DEFAULT -> playbackModeBtn.setGraphic(FontIcon.of(FontAwesomeSolid.REDO, Color.web("#666")));
            case REPEAT -> playbackModeBtn.setGraphic(FontIcon.of(FontAwesomeSolid.REDO, Color.RED));
            case LOOP -> playbackModeBtn.setGraphic(FontIcon.of(FontAwesomeSolid.RETWEET, Color.RED));
        }
    }

    private void updateFetchModeButton(FetchMode fetchMode) {
        fetchModeToggleBtn.setSelected(FetchMode.RANDOM == fetchMode);
    }

    private void handleTrackStopped() {
        trackSlider.setValue(0);
        timeElapsedLabel.setText("0:00");
        ((FontAwesomeIconView) playBtn.getGraphic()).setIcon(FontAwesomeIcon.PLAY);
    }

    @FXML
    private void showQueueView() {
        var navItems = NavigableItemsResolver.resolveQueuedTracks();
        miniPlayerView.fireEvent(new NavigationEvent(navItems));
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

    @FXML
    private void setPlaybackMode() {
        var playbackMode = Player.getPlaybackMode();
        var next = playbackMode.ordinal() + 1;
        if (next >= PlaybackMode.values().length) {
            next = 0;
        }
        Player.setPlaybackMode(PlaybackMode.values()[next]);
    }

    @FXML
    private void volumeOnOff() {
        if (volumeSlider.getValue() > 0) {
            unMuteVolume = volumeSlider.getValue();
            volumeSlider.setValue(0);
        } else {
            volumeSlider.setValue(unMuteVolume < 0 ? 50 : unMuteVolume);
        }
    }
}
