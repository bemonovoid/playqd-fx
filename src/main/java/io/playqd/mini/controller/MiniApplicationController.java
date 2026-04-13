package io.playqd.mini.controller;

import io.playqd.mini.controller.item.*;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import io.playqd.mini.controller.navigator.NavigableItems;
import io.playqd.player.PlayerTrackListManager;
import io.playqd.service.MusicLibrary;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class MiniApplicationController {

    @FXML
    private MiniSearchBarController miniSearchBarController;

    @FXML
    private MiniPlayerViewController miniPlayerViewController;

    @FXML
    private MiniLibraryItemsViewController miniLibraryItemsViewController;

    @FXML
    private void initialize() {
        miniSearchBarController.setOnSearchSubmit((searchScope, searchText) ->
                miniLibraryItemsViewController.showItems(resolveNavigableItems(searchScope, searchText)));
        miniPlayerViewController.setOnQueueViewToggle(selected -> {
            if (selected) {
                miniLibraryItemsViewController.showItems(resolveQueuedNavigableItems());
            } else {
                miniLibraryItemsViewController.refreshLastState();
            }
        });
    }

    private NavigableItems resolveNavigableItems(SearchScope searchScope, String searchText) {
        var token = searchText.toLowerCase();
        return switch (searchScope) {
            case ARTISTS -> {
                Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(MusicLibrary.getArtists().stream()
                        .filter(a -> a.name().toLowerCase().contains(token))
                        .map(ArtistItemRow::new)
                        .toList());
                yield new NavigableItems(ItemsDescriptor.forSearchArtists(token), supplier, ArtistItemRow.class);
            }
            case ALBUMS -> {
                Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(MusicLibrary.getAllAlbums().stream()
                        .filter(a -> a.name().toLowerCase().contains(token))
                        .map(AlbumItemRow::new)
                        .toList());
                yield new NavigableItems(ItemsDescriptor.forSearchAlbums(token), supplier, AlbumItemRow.class);
            }
            case TRACKS -> {
                Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(MusicLibrary.getAllTracks().stream()
                        .filter(t -> t.title().toLowerCase().contains(token))
                        .map(TrackItemRow::new)
                        .toList());
                yield new NavigableItems(ItemsDescriptor.forSearchTracks(token), supplier, TrackItemRow.class);
            }
            case PLAYLISTS, COLLECTIONS -> new NavigableItems(ItemsDescriptor.empty(), Collections::emptyList, null);
        };
    }

    private NavigableItems resolveQueuedNavigableItems() {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                PlayerTrackListManager.trackList().stream().map(QueuedTrackItemRow::new).toList());
        return new NavigableItems(ItemsDescriptor.empty(), supplier, QueuedTrackItemRow.class);
    }

}
