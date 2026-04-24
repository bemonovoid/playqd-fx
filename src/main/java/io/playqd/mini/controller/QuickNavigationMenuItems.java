package io.playqd.mini.controller;

import io.playqd.mini.events.NavigationEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.util.List;

final class QuickNavigationMenuItems {

    static List<MenuItem> get(Node node) {

        var artists = new MenuItem("Artists");
        var albums = new MenuItem("Albums");
        var genres = new MenuItem("Genres");
        var playlists = new MenuItem("Playlists");
        var collections = new MenuItem("Collections");
        var allTracks = new MenuItem("Tracks: all");
        var likedTracks = new MenuItem("Tracks: liked");
        var playedTracks = new MenuItem("Tracks: Recently played");
        var folders = new MenuItem("Folders");

        artists.setOnAction(_ -> node.fireEvent(new NavigationEvent(NavigableItemsResolver.resolveArtists())));
        albums.setOnAction(_ -> node.fireEvent(new NavigationEvent(NavigableItemsResolver.resolveAlbums())));
        genres.setOnAction(_ -> node.fireEvent(new NavigationEvent(NavigableItemsResolver.resolveGenres())));
        playlists.setOnAction(_ -> node.fireEvent(new NavigationEvent(NavigableItemsResolver.resolvePlaylists())));
        collections.setOnAction(_ -> node.fireEvent(new NavigationEvent(NavigableItemsResolver.resolveCollections())));
        allTracks.setOnAction(_ -> node.fireEvent(new NavigationEvent(NavigableItemsResolver.resolveTracks())));
        likedTracks.setOnAction(_ -> node.fireEvent(new NavigationEvent(NavigableItemsResolver.resolveLikedTracks())));
        playedTracks.setOnAction(_ -> node.fireEvent(new NavigationEvent(NavigableItemsResolver.resolvePlayedTracks())));
        folders.setOnAction(_ -> node.fireEvent(new NavigationEvent(NavigableItemsResolver.resolveWatchFolders())));

        var menuItems = List.of(
                artists, albums, playlists, collections,
                new SeparatorMenuItem(),
                allTracks, likedTracks, playedTracks,
                new SeparatorMenuItem(),
                folders);
        var j = 1;
        for (MenuItem item : menuItems) {
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
}
