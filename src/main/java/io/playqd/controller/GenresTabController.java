package io.playqd.controller;

import io.playqd.client.GetGenresResponse;
import io.playqd.data.Genre;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

public class GenresTabController extends SearchResultTabController<Genre, GetGenresResponse> {

    @FXML
    private GenresTab genresTab;

    @FXML
    private TableColumn<Genre, String> nameCol, artistsCountCol, albumsCountCol, tracksCountCol;

    @FXML
    private void initialize() {
        initializeInternal();
    }

    @Override
    protected void initTableInternal() {
        setTableCellValueFactories();
    }

    @Override
    protected void setTableItems(GetGenresResponse response) {
        if (!response.isEmpty()) {
            tableView.setItems(FXCollections.observableList(response.content()));
            var genresPage = response.page();
            genresTab.setText("Genres (" + genresPage.totalElements() + ")");
            pagination.setPageCount(genresPage.totalPages());
            pagination.setCurrentPageIndex(genresPage.number());
        } else if (tableView.getItems() != null) {
            tableView.getItems().clear();
        }
    }

    private void setTableCellValueFactories() {
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name()));
        artistsCountCol.setCellValueFactory(c -> new SimpleStringProperty("" + c.getValue().artistCount()));
        albumsCountCol.setCellValueFactory(c -> new SimpleStringProperty("" + c.getValue().albumCount()));
        tracksCountCol.setCellValueFactory(c -> new SimpleStringProperty("" + c.getValue().trackCount()));
    }
}
