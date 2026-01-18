package io.playqd.controller;

import io.playqd.client.GetArtistsResponse;
import io.playqd.data.Artist;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

public class ArtistsTabController extends SearchResultTabController<Artist, GetArtistsResponse> {

    @FXML
    private TableColumn<Artist, String> nameCol, albumsCountCol, tracksCountCol;

    @FXML
    private void initialize() {
        initializeInternal();
    }

    @Override
    protected void initTableInternal() {
        setTableCellValueFactories();
//        treeTableView.setPlaceholder(createPlaceholder());
    }

    @Override
    protected void initTablePagination() {
        pagination.currentPageIndexProperty().addListener((_, _, newIdx) -> {
//            setTracksTableItems(search(getSearchRequestParams(), newIdx.intValue(), getPageSize()));
        });
    }

    private void setTableCellValueFactories() {
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name()));
        albumsCountCol.setCellValueFactory(c -> new SimpleStringProperty("" + c.getValue().albumsCount()));
        tracksCountCol.setCellValueFactory(c -> new SimpleStringProperty("" + c.getValue().tracksCount()));
    }

    @Override
    protected void setTableItems(GetArtistsResponse response) {

    }
}
