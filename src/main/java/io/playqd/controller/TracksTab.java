package io.playqd.controller;

import io.playqd.client.GetTracksResponse;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;

public class TracksTab extends SearchResultTab<GetTracksResponse> {

    private final TracksTabController controller;

    public TracksTab() {
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.TRACKS_TAB);
        resourceLoader.setRoot(this);
        FXMLLoaderUtils.loadObject(resourceLoader, TracksTab.class);
        this.controller = FXMLLoaderUtils.getController(resourceLoader, TracksTabController.class);
    }

    TracksTabController getController() {
        return controller;
    }

    @Override
    void setItems(GetTracksResponse response) {
        controller.setTableItems(response);
    }
}
