package io.playqd.mini.controller;

import io.playqd.mini.controller.navigator.NavigableItems;
import io.playqd.mini.events.ApplicationEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;

public class MiniApplicationController {

    @FXML
    private Node miniApplicationVBox;

    @FXML
    private MiniSearchBarController miniSearchBarController;

    @FXML
    private MiniPlayerViewController miniPlayerViewController;

    @FXML
    private MiniLibraryItemsViewController miniLibraryItemsViewController;

    @FXML
    private void initialize() {
        setMiniPlayerAppEventHandlers();
        miniSearchBarController.setOnSearchSubmit((searchScope, searchText) ->
                miniLibraryItemsViewController.showItems(resolveNavigableItems(searchScope, searchText)));
        miniPlayerViewController.setOnQueueViewToggle(selected -> {
            if (selected) {
                miniLibraryItemsViewController.showItems(NavigableItemsResolver.resolveQueuedTracks());
            } else {
                miniLibraryItemsViewController.refreshLastState();
            }
        });
        miniLibraryItemsViewController.showItems(NavigableItemsResolver.resolveTracks());
    }

    private NavigableItems resolveNavigableItems(SearchScope searchScope, String searchText) {
        var token = searchText.toLowerCase();
        return switch (searchScope) {
            case ARTISTS -> NavigableItemsResolver.resolveSearchArtists(token);
            case ALBUMS -> NavigableItemsResolver.resolveSearchAlbums(token);
            case TRACKS -> NavigableItemsResolver.resolveSearchTracks(token);
            case PLAYLISTS -> NavigableItemsResolver.resolveSearchPlaylists(token);
            case COLLECTIONS -> NavigableItemsResolver.resolveSearchCollections(token);
        };
    }

    private void setMiniPlayerAppEventHandlers() {
        miniApplicationVBox.addEventHandler(ApplicationEvent.NAVIGATION, event -> {
            event.consume();
            miniLibraryItemsViewController.showItems(event.getNavigableItems());
        });
    }

}
