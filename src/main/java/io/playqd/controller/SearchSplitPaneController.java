package io.playqd.controller;

import javafx.fxml.FXML;

public class SearchSplitPaneController {

    @FXML
    private SearchFiltersVBoxController searchFiltersVBoxController;

    @FXML
    private SearchVBoxController searchVBoxController;

    @FXML
    private void initialize() {
        searchVBoxController.bindFilterControls(searchFiltersVBoxController);
    }

}
