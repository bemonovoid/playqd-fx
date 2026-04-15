package io.playqd.mini.controller.configurer;

import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.NavigableItemsResolver;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public final class PlaylistTracksViewConfigurer extends TracksViewConfigurer {

    public PlaylistTracksViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    protected void configureHeaderLeft(ItemsDescriptor itemsDescriptor, HBox headerLeft) {
        var parent = itemsDescriptor.parent();
        var playlistsHyperLink = new Hyperlink("Playlists");
        playlistsHyperLink.setPadding(new Insets(0));
        playlistsHyperLink.setFocusTraversable(false);
        playlistsHyperLink.setOnAction(_ -> controller.showItems(NavigableItemsResolver.resolvePlaylists()));
        headerLeft.getChildren().addAll(playlistsHyperLink, new Label(": " + parent.getName()));
    }
}
