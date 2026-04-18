package io.playqd.mini.controller.configurer;

import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public final class AlbumTracksViewConfigurer extends TracksViewConfigurer {

    public AlbumTracksViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    protected void configureHeaderLeft(ItemsDescriptor itemsDescriptor, HBox headerLeft) {
        headerLeft.getChildren().add(new Label("Album tracks:"));
    }

}
