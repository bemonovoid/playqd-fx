package io.playqd.controller;

import io.playqd.client.GetArtistsResponse;
import io.playqd.data.Artist;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

import java.util.Collections;

public class ArtistsTabController extends SearchResultTabController<Artist, GetArtistsResponse> {

    @FXML
    private ArtistsTab artistsTab;

    @FXML
    private TableColumn<Artist, String> nameCol, albumsCountCol, tracksCountCol;

    @FXML
    private void initialize() {
        initializeInternal();
    }

    @Override
    protected void initTableInternal() {
        setTableCellValueFactories();
    }

    private void setTableCellValueFactories() {
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name()));
        albumsCountCol.setCellValueFactory(c -> new SimpleStringProperty("" + c.getValue().albumsCount()));
        tracksCountCol.setCellValueFactory(c -> new SimpleStringProperty("" + c.getValue().tracksCount()));
    }

    @Override
    protected final void setTableItems(GetArtistsResponse response) {
        if (!response.isEmpty()) {
            tableView.setItems(FXCollections.observableList(response.content()));
            var artistsPage = response.page();
            artistsTab.setText("Artists (" + artistsPage.totalElements() + ")");
            pagination.setPageCount(artistsPage.totalPages());
            pagination.setCurrentPageIndex(artistsPage.number());
        } else {
            tableView.setItems(FXCollections.observableList(Collections.emptyList()));
            artistsTab.setText("Artists");
            pagination.setPageCount(0);
            pagination.setCurrentPageIndex(0);
        }
    }

}
