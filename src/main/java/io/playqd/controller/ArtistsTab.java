package io.playqd.controller;

import io.playqd.client.GetArtistsResponse;
import io.playqd.data.SearchFlag;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;

import java.util.function.BiConsumer;

public class ArtistsTab extends SearchResultTab<GetArtistsResponse> {

    private final ArtistsTabController controller;

    public ArtistsTab() {
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.ARTISTS_TAB);
        resourceLoader.setRoot(this);
        FXMLLoaderUtils.loadObject(resourceLoader, ArtistsTab.class);
        this.controller = FXMLLoaderUtils.getController(resourceLoader, ArtistsTabController.class);
    }

    @Override
    void setItems(GetArtistsResponse response) {
        controller.setTableItems(response);
    }

    @Override
    void onPageChanged(BiConsumer<SearchFlag, Integer> consumer) {
        controller.pagination.currentPageIndexProperty().addListener((_, _, newIdx) ->
                consumer.accept(SearchFlag.SEARCH_IN_ARTISTS, newIdx.intValue()));
    }

}
