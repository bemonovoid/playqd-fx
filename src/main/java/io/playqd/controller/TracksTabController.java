package io.playqd.controller;

import io.playqd.client.GetTracksResponse;
import io.playqd.data.Track;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;

import java.time.format.DateTimeFormatter;

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
        initTablePagination();
    }

    @Override
    protected final void initTableInternal() {
        setTableCellValueFactories();
//        treeTableView.setPlaceholder(createPlaceholder());
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
    protected final void initTablePagination() {
        pagination.currentPageIndexProperty().addListener((_, _, newIdx) -> {
//            setTracksTableItems(search(getSearchRequestParams(), newIdx.intValue(), getPageSize()));
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
        }
    }

}
