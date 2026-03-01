package io.playqd.controller.music;

import io.playqd.data.Album;
import io.playqd.utils.FakeIds;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class MusicLibraryAlbumsController extends MusicLibraryArtistsController {

    private static final Logger LOG = LoggerFactory.getLogger(MusicLibraryAlbumsController.class);

    private final Searchable searchable = new SearchTextController();

    @FXML
    private Label albumsSearchLabel;

    @FXML
    private Label albumsInfoLabel;

    protected void initializeInternal() {
        super.initializeInternal();
        searchable.initialize(albumsListView, onSearchTextInputChanged(), onSearchTextInputCleared());
        albumsListView.getSelectionModel().selectedItemProperty().addListener((_, oldAlbum, selectedAlbum) -> {
            if (oldAlbum != null && selectedAlbum == null) {
                getTracksContainer().clearTracksTable();
            }
            if (selectedAlbum != null) { // is null when some album is selected but next selection is artist list view
                getTracksContainer().getTracksTableView().setItems(
                        FXCollections.observableList(getAlbumTracks(selectedAlbum)));
            }
        });
        initAlbumsInfoLabelListener();
    }

    private Consumer<String> onSearchTextInputChanged() {
        return newInput -> {
            albumsListView.getSelectionModel().clearSelection();
            if (newInput.isEmpty()) {
                albumsSearchLabel.setText("");
                albumsSearchLabel.setVisible(false);
                return;
            }
            @SuppressWarnings("unchecked")
            var itemsStream = ((List<Album>) albumsListView.getUserData()).stream()
                    .filter(album -> !album.name().equals(FakeIds.ALL_ARTIST_ALBUMS_NAME))
                    .filter(album -> album.name().toLowerCase().contains(newInput));
            var selectedArtist = getSelectedArtist();
            if (FakeIds.ALL_ARTIST != selectedArtist.id()) {
                itemsStream = itemsStream.filter(album -> selectedArtist.name().equals(album.artistName()));
            }
            var items = new ArrayList<>(itemsStream.toList());
            LOG.info("Search by: '{}'. Found: {}", newInput, items.size());
            items.sort(Comparator.comparing(a -> a.name().toLowerCase()));
            albumsListView.setItems(FXCollections.observableArrayList(items));
            albumsListView.getSelectionModel().selectFirst();
            if (!albumsSearchLabel.isVisible()) {
                albumsSearchLabel.setVisible(true);
            }
            albumsSearchLabel.setText(newInput);
        };
    }

    private Runnable onSearchTextInputCleared() {
        return () -> {
            @SuppressWarnings("unchecked")
            var sourceItems = (List<Album>) albumsListView.getUserData();
            var selectedArtist = getSelectedArtist();
            if (FakeIds.ALL_ARTIST != selectedArtist.id()) {
                sourceItems = sourceItems.stream()
                        .filter(album -> selectedArtist.name().equals(album.artistName()))
                        .toList();
            }
            albumsListView.setItems(FXCollections.observableArrayList(sourceItems));
            albumsListView.getSelectionModel().selectFirst();
        };
    }

    private void initAlbumsInfoLabelListener() {
        albumsInfoLabel.setDisable(true);
        albumsInfoLabel.setStyle("-fx-font-size: 11px;");
        albumsInfoLabel.setOpacity(0.6);
        albumsListView.itemsProperty().addListener((_, _, newItems) -> {
            if (newItems == null || newItems.isEmpty()) {
                albumsInfoLabel.setText("");
            } else {
                var albumsText = newItems.size() > 1 ? "albums" : "album";
                albumsInfoLabel.setText(newItems.size() + " " + albumsText);
            }
        });
    }
}
