package io.playqd.controller.music;

import io.playqd.client.PlayqdClientProvider;
import io.playqd.controller.view.ArtistsContainer;
import io.playqd.data.Artist;
import io.playqd.utils.FakeIds;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

final class ArtistContainerManager extends SearchableView {

    private static final Logger LOG = LoggerFactory.getLogger(ArtistContainerManager.class);

    private final MusicSplitPaneController musicViewController;
    private final ArtistsContainer artistsContainer;
    private final ListView<Artist> artistsListView;

    ArtistContainerManager(MusicSplitPaneController musicViewController) {
        this.musicViewController = musicViewController;
        this.artistsContainer = musicViewController.getArtistsContainer();
        this.artistsListView = musicViewController.getArtistsContainer().getArtistsListView();
    }

    void initialize() {
        super.initialize(artistsListView, onSearchTextInputChanged(), onSearchTextInputCleared());
        artistsListView.getSelectionModel().selectedItemProperty().addListener((_, oldArtist, selectedArtist) -> {
            if (oldArtist != null && selectedArtist == null) {
                musicViewController.clearAlbumsList();
                musicViewController.getTracksContainer().clearTracksTable();
            }
            if (selectedArtist != null) {
                musicViewController.getAlbumsListView().setCellFactory(
                        new AlbumsListViewCellFactory(new AlbumsListViewCellFactoryListener(musicViewController)));
                if (FakeIds.ALL_ARTIST.equals(selectedArtist.id())) {
                    musicViewController.showAllAlbums();
                    musicViewController.showAllTracks();
                } else {
                    musicViewController.showArtistAlbums(selectedArtist);
                }
            }
        });
        showAllArtists();
    }

    @Override
    public Consumer<String> onSearchTextInputChanged() {
        return newInput -> {
            artistsListView.getSelectionModel().clearSelection();
            if (newInput.isEmpty()) {
                return;
            }
            @SuppressWarnings("unchecked")
            var items = new ArrayList<>((List<Artist>) artistsListView.getUserData());
            items.removeIf(artist -> !artist.name().toLowerCase().contains(newInput));
            LOG.info("Search by: '{}'. Found: {}", newInput, items.size());
            if (items.isEmpty()) {
                artistsListView.getItems().clear();
            } else {
                items.sort(Comparator.comparing(a -> a.name().toLowerCase()));
                artistsListView.setItems(FXCollections.observableArrayList(items));
                artistsListView.getSelectionModel().selectFirst();
            }
        };
    }

    @Override
    public Runnable onSearchTextInputCleared() {
        return () -> {
            @SuppressWarnings("unchecked")
            var sourceItems = (List<Artist>) artistsListView.getUserData();
            artistsListView.setItems(FXCollections.observableArrayList(sourceItems));
            artistsListView.getSelectionModel().selectFirst();
        };
    }

    private void showAllArtists() {
        Platform.runLater(() -> {
            artistsListView.setCellFactory(new ArtistsListViewCellFactory());
            var artists = PlayqdClientProvider.get().getArtists().stream()
                    .sorted(Comparator.comparing(Artist::name))
                    .toList();

            var allArtistsArtist = createFakeAllArtistsArtist(artists.size());

            var result = new ArrayList<Artist>(artists.size() + 1);
            result.add(allArtistsArtist);
            result.addAll(artists);

            artistsListView.setUserData(Collections.unmodifiableList(result));
            artistsListView.setItems(FXCollections.observableArrayList(result));
            artistsListView.getSelectionModel().selectFirst();
        });
    }

    static Artist createFakeAllArtistsArtist(int totalArtists) {
        return new Artist(FakeIds.ALL_ARTIST, "All Artists", totalArtists, -1);
    }

}
