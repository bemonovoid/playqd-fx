package io.playqd.mini.controller;

import java.util.function.Predicate;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.QueuedTrackItemRow;
import io.playqd.mini.controller.navigator.NavigableItems;
import io.playqd.mini.events.ApplicationEvent;
import io.playqd.player.Player;
import io.playqd.player.PlayerTrack;

public class MiniApplicationController {

    @FXML
    private VBox miniApplicationVBox;

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
        miniLibraryItemsViewController.showItems(NavigableItemsResolver.resolveTracks());
    }

    private NavigableItems resolveNavigableItems(SearchScope searchScope, SearchToken token) {
        return switch (searchScope) {
            case ARTISTS -> NavigableItemsResolver.resolveSearchArtists(token);
            case ALBUMS -> NavigableItemsResolver.resolveSearchAlbums(token);
            case COLLECTIONS -> NavigableItemsResolver.resolveSearchCollections(token);
            case FOLDERS -> null;
            case GENRES -> null;
            case PLAYLISTS -> NavigableItemsResolver.resolveSearchPlaylists(token);
            case TRACKS -> NavigableItemsResolver.resolveSearchTracks(token);
        };
    }

    private void setMiniPlayerAppEventHandlers() {
        miniApplicationVBox.addEventHandler(ApplicationEvent.NAVIGATION, event -> {
            event.consume();
            var navItems = event.getNavigableItems();
            if  (navItems.type() == QueuedTrackItemRow.class) {
                miniLibraryItemsViewController.showItems(navItems, selectIfPlaying());
            } else {
                miniLibraryItemsViewController.showItems(navItems);
            }
        });
    }

    private static Predicate<LibraryItemRow> selectIfPlaying() {
        return libraryItemRow -> Player.playerTrack()
                .map(PlayerTrack::track)
                .filter(track -> track.id() == libraryItemRow.getId())
                .isPresent();
    }

}
