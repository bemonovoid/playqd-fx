package io.playqd.controller;

import io.playqd.client.GetAlbumsResponse;
import io.playqd.data.Album;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

import java.util.Collections;

public class AlbumsTabController extends SearchResultTabController<Album, GetAlbumsResponse> {

    @FXML
    private AlbumsTab albumsTab;

    @FXML
    private TableColumn<Album, String> nameCol, releaseDateCol, genreCol, tracksCountCol;

    @FXML
    private void initialize() {
        initializeInternal();
    }

    @Override
    protected void initTableInternal() {
        setTableCellValueFactories();
    }

    @Override
    protected void setTableItems(GetAlbumsResponse response) {
        if (!response.isEmpty()) {
            tableView.setItems(FXCollections.observableList(response.content()));
            var albumsPage = response.page();
            albumsTab.setText("Albums (" + albumsPage.totalElements() + ")");
            pagination.setPageCount(albumsPage.totalPages());
            pagination.setCurrentPageIndex(albumsPage.number());
        } else {
            tableView.setItems(FXCollections.observableList(Collections.emptyList()));
            albumsTab.setText("Albums");
            pagination.setPageCount(0);
            pagination.setCurrentPageIndex(0);
        }
    }

    private void setTableCellValueFactories() {
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name()));
        releaseDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().releaseDate()));
        tracksCountCol.setCellValueFactory(c -> new SimpleStringProperty("" + c.getValue().tracksCount()));
        genreCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().genre()));
    }
}
