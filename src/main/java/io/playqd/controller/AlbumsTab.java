package io.playqd.controller;

import io.playqd.client.GetAlbumsResponse;
import io.playqd.data.SearchFlag;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;

import java.util.function.BiConsumer;

public class AlbumsTab extends SearchResultTab<GetAlbumsResponse> {

    private final AlbumsTabController controller;

    public AlbumsTab() {
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.ALBUMS_TAB);
        resourceLoader.setRoot(this);
        FXMLLoaderUtils.loadObject(resourceLoader, AlbumsTab.class);
        this.controller = FXMLLoaderUtils.getController(resourceLoader, AlbumsTabController.class);
    }

    @Override
    void setItems(GetAlbumsResponse response) {
        controller.setTableItems(response);
    }

    @Override
    void onPageChanged(BiConsumer<SearchFlag, Integer> consumer) {
        controller.pagination.currentPageIndexProperty().addListener((_, _, newIdx) ->
                consumer.accept(SearchFlag.SEARCH_IN_ALBUMS, newIdx.intValue()));
    }

}
