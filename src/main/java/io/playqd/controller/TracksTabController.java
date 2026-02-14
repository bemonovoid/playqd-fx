package io.playqd.controller;

import io.playqd.client.GetTracksResponse;
import io.playqd.data.Track;
import io.playqd.player.PlayRequest;
import io.playqd.player.PlayerEngine;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseButton;

import java.time.format.DateTimeFormatter;
import java.util.Collections;

public class TracksTabController extends SearchResultTabController<Track, GetTracksResponse> {

    @FXML
    private Tab tracksTab;

    @FXML
    private TableColumn<Track, String> titleCol, artistCol, albumCol, filenameCol, lengthCol, genreCol, sizeCol,
            extensionCol, bitRateCol, sampleRateCol, bitsPerSampleCol, ratingCol, mimeTypeCol, playCountCol,
            lastPlayedDateCol, addedDateCol;

    @FXML
    private void initialize() {
        initTableInternal();
        tableView.setRowFactory(_ -> {
            var row = new TableRow<Track>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && MouseButton.PRIMARY == e.getButton() && e.getClickCount() == 2) {
//                    PlayerEngine.play(PlayRequest.singleTrack(row.getItem()));
                }
            });
            return row;
        });
    }

    @Override
    protected final void initTableInternal() {
        setTableCellValueFactories();
    }

    private void setTableCellValueFactories() {
        titleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().title()));
        artistCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().artist().name()));
        albumCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().album().name()));
        genreCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().album().genre()));
        filenameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fileAttributes().name()));
        lengthCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().length().readable()));
        sizeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fileAttributes().size()));
        extensionCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fileAttributes().extension()));
        mimeTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().audioFormat().mimeType()));
        bitRateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().audioFormat().bitRate()));
        sampleRateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().audioFormat().sampleRate()));
        bitsPerSampleCol.setCellValueFactory(c -> new SimpleStringProperty("" + c.getValue().audioFormat().bitsPerSample()));
        ratingCol.setCellValueFactory(c -> new SimpleStringProperty("" + c.getValue().rating().value()));
        playCountCol.setCellValueFactory(c -> new SimpleStringProperty("" + c.getValue().playback().count()));
        lastPlayedDateCol.setCellValueFactory(c -> {
            if (c.getValue().playback().lastPlayedDate() != null) {
                var date = c.getValue().playback().lastPlayedDate();
                return new SimpleStringProperty(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
            return new SimpleStringProperty("");
        });
        addedDateCol.setCellValueFactory(c -> {
            if (c.getValue().additionalInfo().addedToWatchFolderDate() != null) {
                var date = c.getValue().additionalInfo().addedToWatchFolderDate();
                return new SimpleStringProperty(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
            return new SimpleStringProperty("");
        });
    }

    @Override
    protected final void setTableItems(GetTracksResponse response) {
        if (!response.isEmpty()) {
            tableView.setItems(FXCollections.observableList(response.content()));
            var tracksPage = response.page();
            tracksTab.setText("Tracks (" + tracksPage.totalElements() + ")");
            pagination.setPageCount(tracksPage.totalPages());
            pagination.setCurrentPageIndex(tracksPage.number());
        } else {
            tableView.setItems(FXCollections.observableList(Collections.emptyList()));
            tracksTab.setText("Tracks");
            pagination.setPageCount(0);
            pagination.setCurrentPageIndex(0);
        }
    }

}
