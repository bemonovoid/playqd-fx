package io.playqd.controller.music;

import io.playqd.client.PlayqdClientProvider;
import io.playqd.controller.view.TracksContainer;
import io.playqd.data.Album;
import io.playqd.data.Artist;
import io.playqd.data.Track;
import io.playqd.utils.FakeIds;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

import java.util.*;
import java.util.stream.Collectors;

public abstract class MusicSplitPaneController {

    @FXML
    protected ListView<Artist> artistsListView;

    @FXML
    protected ListView<Album> albumsListView;

    @FXML
    protected TracksContainer tracksContainer;

    protected void initializeInternal() {

    }

    private void initialize() {
//        new TracksTableViewController(this).initialize();
//        new AlbumsListController(this).initialize();
//        new ArtistContainerManager(this).initialize();
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

    List<Track> getArtistTracks(long trackId) {
        return PlayqdClientProvider.get().getTracksByArtistId(trackId).stream()
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

    protected final Artist getSelectedArtist() {
        return artistsListView.getSelectionModel().getSelectedItem();
    }

    protected final ListView<Album> getAlbumsListView() {
        return albumsListView;
    }

    protected final TracksContainer getTracksContainer() {
        return tracksContainer;
    }

    protected final TableView<Track> getTracksTableView() {
        return getTracksContainer().getTracksTableView();
    }

    protected final Album getSelectedAlbum() {
        return getAlbumsListView().getSelectionModel().getSelectedItem();
    }

    static Album createFakeArtistAlbumsAlbum(Album artistAlbum) {
        return new Album(
                artistAlbum.id(), FakeIds.ALL_ARTIST_ALBUMS_NAME, "", "", artistAlbum.artistName(), null, 0, 0);
    }
}
