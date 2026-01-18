package io.playqd.controller;

import io.playqd.client.GetSearchResponse;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class SearchResultTabPaneController {

    @FXML
    private TabPane searchResultTabPane;

    @FXML
    TracksTab tracksTab;

    @FXML
    ArtistsTab artistsTab;

    void setItems(GetSearchResponse response) {
        tracksTab.setItems(response.tracks());
        artistsTab.setItems(response.artists());
    }

}
