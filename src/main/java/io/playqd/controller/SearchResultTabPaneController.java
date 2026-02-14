package io.playqd.controller;

import io.playqd.client.GetSearchResponse;
import io.playqd.data.SearchFlag;
import javafx.fxml.FXML;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SearchResultTabPaneController {

    @FXML
    private TracksTab tracksTab;

    @FXML
    private ArtistsTab artistsTab;

    @FXML
    private AlbumsTab albumsTab;

    @FXML
    private GenresTab genresTab;

    void setItems(GetSearchResponse response) {
        tracksTab.setItems(response.tracks());
        artistsTab.setItems(response.artists());
        albumsTab.setItems(response.albums());
        genresTab.setItems(response.genres());
    }

    void onPageChanged(BiConsumer<SearchFlag, Integer> consumer) {
        tracksTab.onPageChanged(consumer);
        artistsTab.onPageChanged(consumer);
        albumsTab.onPageChanged(consumer);
        genresTab.onPageChanged(consumer);
    }

}
