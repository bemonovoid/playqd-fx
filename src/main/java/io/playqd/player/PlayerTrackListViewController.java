package io.playqd.player;

import io.playqd.data.Track;
import io.playqd.utils.TimeUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class PlayerTrackListViewController extends PlayerTrackListView {

    @FXML
    private ListView<Track> trackListView;

    @FXML
    private Label trackNameLabel, artistNameLabel, albumNameLabel, albumInfoLabel, fileInfoLabel, fileLocationLabel;

    @Override
    void setItems(List<Track> tracks) {
        trackListView.setCellFactory(new PlayerTrackListViewCellFactory());
        trackListView.setItems((FXCollections.observableArrayList(new ArrayList<>(tracks))));
    }

    @FXML
    private void initialize() {
        PlayerTrackListManager.setPlayerTrackListView(this);
        initTooltips();
        Player.onPlayingTrackChanged(playingTrack ->
                Platform.runLater(() ->
                        trackListView.getItems().stream()
                                .filter(track -> track.id() == playingTrack.id())
                                .findFirst()
                                .ifPresent(track -> {
                                    trackListView.getSelectionModel().select(track);
                                    updateTrackInfoView(track);
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