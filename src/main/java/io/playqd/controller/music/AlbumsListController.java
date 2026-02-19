package io.playqd.controller.music;

import io.playqd.data.Album;
import io.playqd.utils.FakeIds;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

final class AlbumsListController extends SearchableView {

    private static final Logger LOG = LoggerFactory.getLogger(AlbumsListController.class);

    private final MusicSplitPaneController musicViewController;
    private final ListView<Album> albumsListView;

    AlbumsListController(MusicSplitPaneController musicViewController) {
        this.musicViewController = musicViewController;
        this.albumsListView = musicViewController.getAlbumsListView();
    }

    void initialize() {
        super.initialize(albumsListView, onSearchTextInputChanged(), onSearchTextInputCleared());
        albumsListView.getSelectionModel().selectedItemProperty().addListener((_, oldAlbum, selectedAlbum) -> {
            if (oldAlbum != null && selectedAlbum == null) {
                musicViewController.getTracksContainer().clearTracksTable();
            }
            if (selectedAlbum != null) { // is null when some album is selected but next selection is artist list view
                musicViewController.getTracksContainer().getTracksTableView().setItems(
                        FXCollections.observableList(musicViewController.getAlbumTracks(selectedAlbum)));
            }
        });
    }

    @Override
    public Consumer<String> onSearchTextInputChanged() {
        return newInput -> {
            albumsListView.getSelectionModel().clearSelection();
            if (newInput.isEmpty()) {
                return;
            }
            @SuppressWarnings("unchecked")
            var itemsStream = ((List<Album>) albumsListView.getUserData()).stream()
                    .filter(album -> album.name().toLowerCase().contains(newInput));
            var selectedArtist = musicViewController.getArtistsContainer().getSelectedArtist();
            if (!FakeIds.ALL_ARTIST.equals(selectedArtist.id())) {
                itemsStream = itemsStream.filter(album -> selectedArtist.name().equals(album.artistName()));
            }
            var items = new ArrayList<>(itemsStream.toList());
            LOG.info("Search by: '{}'. Found: {}", newInput, items.size());
            items.sort(Comparator.comparing(a -> a.name().toLowerCase()));
            albumsListView.setItems(FXCollections.observableArrayList(items));
            albumsListView.getSelectionModel().selectFirst();
        };
    }

    @Override
    public Runnable onSearchTextInputCleared() {
        return () -> {
            @SuppressWarnings("unchecked")
            var sourceItems = (List<Album>) albumsListView.getUserData();
            var selectedArtist = musicViewController.getArtistsContainer().getSelectedArtist();
            if (!FakeIds.ALL_ARTIST.equals(selectedArtist.id())) {
                sourceItems = sourceItems.stream()
                        .filter(album -> selectedArtist.name().equals(album.artistName()))
                        .toList();
            }
            albumsListView.setItems(FXCollections.observableArrayList(sourceItems));
            albumsListView.getSelectionModel().selectFirst();
        };
    }
}
