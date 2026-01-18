package io.playqd.controller;

import io.playqd.client.GetArtistsResponse;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;

public class ArtistsTab extends SearchResultTab<GetArtistsResponse> {

    private final ArtistsTabController controller;

    public ArtistsTab() {
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.ARTISTS_TAB);
        resourceLoader.setRoot(this);
        FXMLLoaderUtils.loadObject(resourceLoader, ArtistsTab.class);
        this.controller = FXMLLoaderUtils.getController(resourceLoader, ArtistsTabController.class);
    }

    ArtistsTabController getController() {
        return controller;
    }

    @Override
    void setItems(GetArtistsResponse response) {
        controller.setTableItems(response);
    }

}
