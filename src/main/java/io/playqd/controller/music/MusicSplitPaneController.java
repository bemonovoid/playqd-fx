package io.playqd.controller.music;

import io.playqd.client.PageRequest;
import io.playqd.client.PlayqdClientProvider;
import io.playqd.controller.view.ArtistsContainer;
import io.playqd.controller.view.TracksContainer;
import io.playqd.data.Album;
import io.playqd.data.Artist;
import io.playqd.data.Track;
import io.playqd.utils.FakeIds;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.*;
import java.util.stream.Collectors;

public class MusicSplitPaneController {

    @FXML
    private ArtistsContainer artistsContainer;

    @FXML
    private ListView<Album> albumsListView;

    @FXML
    private TracksContainer tracksContainer;

    @FXML
    private void initialize() {
        new TracksTableViewController(this).initialize();
        new AlbumsListController(this).initialize();
        new ArtistContainerManager(this).initialize();
    }

    void showAllAlbums() {
        Platform.runLater(() -> {
            var albums = PlayqdClientProvider.get().getAlbums();
            var albumsGroupedByArtist = albums.stream().collect(Collectors.groupingBy(Album::artistName));
            var albumsListViewItems = new ArrayList<Album>(albums.size() + albumsGroupedByArtist.size());
            albumsGroupedByArtist.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        var headerAlbum = createFakeArtistAlbumsAlbum(entry.getValue().getFirst());
                        albumsListViewItems.add(headerAlbum);
                        albumsListViewItems.addAll(entry.getValue());
                    });
            albumsListView.setUserData(Collections.unmodifiableList(albumsListViewItems));
            albumsListView.setItems(FXCollections.observableArrayList(albumsListViewItems));
        });
    }

    void showArtistAlbums(Artist selectedArtist) {
        Platform.runLater(() -> {
            var albums = PlayqdClientProvider.get().getAlbumsByArtistId(selectedArtist.id());
            var albumsListViewItems = new ArrayList<Album>(albums.size() + 1);
            var headerAlbum = createFakeArtistAlbumsAlbum(albums.getFirst());
            albumsListViewItems.add(headerAlbum);
            albumsListViewItems.addAll(albums);
            albumsListView.setItems(FXCollections.observableArrayList(albumsListViewItems));
            tracksContainer.getTracksTableView().setItems(FXCollections.observableList(getArtistTracks(selectedArtist.id())));
        });
    }

    void showAllTracks() {
        Platform.runLater(() -> {
            var allTracks = getAllTracks();
            tracksContainer.getTracksTableView().setUserData(Collections.unmodifiableList(allTracks));
            tracksContainer.getTracksTableView().setItems(FXCollections.observableList(allTracks));
        });
    }

    List<Track> getAllTracks() {
        return new ArrayList<>(PlayqdClientProvider.get().getAllTracks(PageRequest.unpaged()).content());
    }

    List<Track> getArtistTracks(String artistId) {
        return PlayqdClientProvider.get().getTracksByArtistId(artistId).stream()
                .sorted(Comparator.comparing(Track::title))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    List<Track> getAlbumTracks(Album album) {

        var albumTracks = PlayqdClientProvider.get().getTracksByAlbumId(album.id());

        return albumTracks.stream()
                .sorted((t1, t2) -> {
                    try {
                        if (t1.number() == null || t2.number() == null) {
                            return 0;
                        }
                        return Integer.compare(Integer.parseInt(t1.number()), Integer.parseInt(t2.number()));
                    } catch (NumberFormatException e) {
                        return t1.number().compareTo(t2.number());
                    }
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    void clearAlbumsList() {
        albumsListView.getItems().clear();
    }

    ArtistsContainer getArtistsContainer() {
        return artistsContainer;
    }

    ListView<Album> getAlbumsListView() {
        return albumsListView;
    }

    TracksContainer getTracksContainer() {
        return tracksContainer;
    }

    Album getSelectedAlbum() {
        return getAlbumsListView().getSelectionModel().getSelectedItem();
    }

    static Album createFakeArtistAlbumsAlbum(Album artistAlbum) {
        return new Album(
                artistAlbum.id(), FakeIds.ALL_ARTIST_ALBUMS, "", "", artistAlbum.artistName(), false, null, 0, 0);
    }
}
