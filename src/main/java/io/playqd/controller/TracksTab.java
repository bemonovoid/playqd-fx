package io.playqd.controller;

import io.playqd.client.GetTracksResponse;
import io.playqd.data.SearchFlag;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;

import java.util.function.BiConsumer;

public class TracksTab extends SearchResultTab<GetTracksResponse> {

    private final TracksTabController controller;

    public TracksTab() {
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.TRACKS_TAB);
        resourceLoader.setRoot(this);
        FXMLLoaderUtils.loadObject(resourceLoader, TracksTab.class);
        this.controller = FXMLLoaderUtils.getController(resourceLoader, TracksTabController.class);
    }

    @Override
    void setItems(GetTracksResponse response) {
        controller.setTableItems(response);
    }

    @Override
    void onPageChanged(BiConsumer<SearchFlag, Integer> consumer) {
        controller.pagination.currentPageIndexProperty().addListener((_, _, newIdx) ->
                consumer.accept(SearchFlag.SEARCH_IN_TRACKS, newIdx.intValue()));
    }
}
