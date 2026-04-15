package io.playqd.mini.controller;

import io.playqd.mini.events.NavigationEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class Accelerators {

    private static final Logger LOGGER = LoggerFactory.getLogger(Accelerators.class);

    private static final String SEARCH_INPUT_TEXT_FLD_ID = "searchInputTextFld";
    private static final String ITEMS_TABLE_HEADER_NODE_ID = "itemsTableHeaderToolBar";

    private static boolean INITIALIZED = false;

    private Accelerators() {

    }

    public static void initialize(Scene scene) {
        if (INITIALIZED) {
            LOGGER.warn("Accelerators already initialized");
            return;
        }
        createQuickNavigationAccelerator(scene);
        createRequestSearchInputFocusAccelerator(scene);
        INITIALIZED = true;
    }

    private static void createQuickNavigationAccelerator(Scene scene) {
        var keyCodeCombination = new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(keyCodeCombination,  () -> {
            var node = scene.lookup("#" + ITEMS_TABLE_HEADER_NODE_ID);
            var bounds = node.localToScreen(node.getBoundsInLocal());
            double centerX = bounds.getMinX() + (bounds.getWidth() / 2);
            double centerY = bounds.getMinY() + (bounds.getHeight() / 2);
            var contextMenu = new ContextMenu();
            contextMenu.getStyleClass().add("nav-context-menu");
            contextMenu.getItems().addAll(buildQuickNavigationMenuItems(node));
            contextMenu.show(node, centerX, centerY);
        });
    }

    private static List<MenuItem> buildQuickNavigationMenuItems(Node node) {
        var artists = new MenuItem("Artists");
        var albums = new MenuItem("Albums");
        var genres = new MenuItem("Genres");
        var playlists = new MenuItem("Playlists");
        var allTracks = new MenuItem("Tracks: all");
        var likedTracks = new MenuItem("Tracks: liked");
        var playedTracks = new MenuItem("Tracks: Recently played");
        var folders = new MenuItem("Folders");

        artists.setOnAction(_ -> node.fireEvent(new NavigationEvent(NavigableItemsResolver.resolveArtists())));
        albums.setOnAction(_ -> node.fireEvent(new NavigationEvent(NavigableItemsResolver.resolveAlbums())));
        genres.setOnAction(_ -> node.fireEvent(new NavigationEvent(NavigableItemsResolver.resolveGenres())));
        playlists.setOnAction(_ -> node.fireEvent(new NavigationEvent(NavigableItemsResolver.resolvePlaylists())));
        allTracks.setOnAction(_ -> node.fireEvent(new NavigationEvent(NavigableItemsResolver.resolveTracks())));
        likedTracks.setOnAction(_ -> node.fireEvent(new NavigationEvent(NavigableItemsResolver.resolveTracks())));
        playedTracks.setOnAction(_ -> node.fireEvent(new NavigationEvent(NavigableItemsResolver.resolveTracks())));

        var menuItems = List.of(
                artists, albums, playlists,
                new SeparatorMenuItem(),
                allTracks, likedTracks, playedTracks,
                new SeparatorMenuItem(),
                folders);
        var j = 1;
        for (int i = 0; i < menuItems.size(); i++) {
            var item = menuItems.get(i);
            if (item instanceof SeparatorMenuItem) {
                continue;
            }
            item.setGraphic(buildMnemonicLabel("" + j++));
        }

        return menuItems;
    }

    private static Label buildMnemonicLabel(String character) {
        var label = new Label("_" + character + ".");
        label.setMnemonicParsing(true);
        label.setStyle("-fx-font-size: 12");
        return label;
    }

    private static void createRequestSearchInputFocusAccelerator(Scene scene) {
        var keyCodeCombination = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(keyCodeCombination,  () ->
                scene.lookup("#" + SEARCH_INPUT_TEXT_FLD_ID).requestFocus());
    }
}
