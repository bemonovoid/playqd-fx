package io.playqd.controller;

import io.playqd.client.GetGenresResponse;
import io.playqd.data.SearchFlag;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;

import java.util.function.BiConsumer;

public class GenresTab extends SearchResultTab<GetGenresResponse> {

    private final GenresTabController controller;

    public GenresTab() {
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.GENRES_TAB);
        resourceLoader.setRoot(this);
        FXMLLoaderUtils.loadObject(resourceLoader, GenresTab.class);
        this.controller = FXMLLoaderUtils.getController(resourceLoader, GenresTabController.class);
    }

    @Override
    void setItems(GetGenresResponse response) {
        controller.setTableItems(response);
    }

    @Override
    void onPageChanged(BiConsumer<SearchFlag, Integer> consumer) {
        controller.pagination.currentPageIndexProperty().addListener((_, _, newIdx) ->
                consumer.accept(SearchFlag.SEARCH_IN_GENRES, newIdx.intValue()));
    }

}
