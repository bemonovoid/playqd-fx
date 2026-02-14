package io.playqd.controller.music;

import io.playqd.client.GetArtistsRequest;
import io.playqd.client.PageRequest;
import io.playqd.client.PlayqdClientProvider;
import io.playqd.data.Artist;
import io.playqd.data.Track;
import io.playqd.event.MouseEventHelper;
import io.playqd.player.AlbumListViewActionListener;
import io.playqd.player.PlayRequest;
import io.playqd.player.PlayerEngine;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MusicSplitPaneController {

    @FXML
    private ListView<Artist> artistsListView;

    @FXML
    private ListView<Track.Album> albumsListView;

    @FXML
    private TableView<Track> tracksTableView;

    @FXML
    private TableColumn<Track, String> trackNumberCol, titleCol, timeCol;

    @FXML
    private void initialize() {

        tracksTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);
        tracksTableView.setRowFactory(_ -> {
            var row = new TableRow<Track>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && MouseEventHelper.primaryButtonDoubleClicked(e)) {
                    PlayerEngine.enqueueAndPlay(new PlayRequest(tracksTableView.getItems(), row.getIndex()));
                }
            });
            return row;
        });
        artistsListView.setCellFactory(new ArtistsListViewCellFactory());

        var artists = PlayqdClientProvider.get().getArtists(GetArtistsRequest.createDefault(), PageRequest.defaultPage());

        var allArtistsArtist = new Artist("all-artists", "All Artists", artists.content().size(), -1);

        var result = new ArrayList<Artist>(artists.content().size() + 1);
        result.add(allArtistsArtist);
        result.addAll(artists.content());

        artistsListView.setItems(FXCollections.observableArrayList(result));


        artistsListView.getSelectionModel().selectedItemProperty().addListener((_, _, selectedArtist) -> {
            albumsListView.setCellFactory(new AlbumsListViewCellFactory(new AlbumsListViewActionListenerImpl()));
            var tracks = PlayqdClientProvider.get().getTracksByArtistId(selectedArtist.id());

            var albumTracks = tracks.content().stream().collect(Collectors.groupingBy(Track::album));
            albumsListView.setItems(FXCollections.observableArrayList(new ArrayList<>(albumTracks.keySet())));

            tracksTableView.setItems(FXCollections.observableList(tracks.content()));
            tracksTableView.setUserData(tracks.content());
        });

        albumsListView.getSelectionModel().selectedItemProperty().addListener((_, _, selectedAlbum) -> {
            tracksTableView.setItems(FXCollections.observableList(getAlbumTracks(selectedAlbum)));
        });


        trackNumberCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().number()));
        titleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().title()));
        timeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().length().readable()));
    }

    private List<Track> getAlbumTracks(Track.Album album) {

        @SuppressWarnings("unchecked")
        var artistTracks = (List<Track>) tracksTableView.getUserData();

        return artistTracks.stream()
                .filter(track -> track.album().id().equals(album.id()))
                .sorted((t1, t2) -> {
                    try {
                        return Integer.compare(Integer.parseInt(t1.number()), Integer.parseInt(t2.number()));
                    } catch (NumberFormatException e) {
                        return t1.number().compareTo(t2.number());
                    }
                })
                .toList();
    }

    private class AlbumsListViewActionListenerImpl implements AlbumListViewActionListener {

        @Override
        public void onAlbumDoubleClicked(Track.Album album) {
            PlayerEngine.enqueueAndPlay(new PlayRequest(tracksTableView.getItems()));
        }

    }
}
