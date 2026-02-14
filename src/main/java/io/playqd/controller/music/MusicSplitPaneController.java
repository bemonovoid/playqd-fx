package io.playqd.controller.music;

import io.playqd.client.GetArtistsRequest;
import io.playqd.client.PageRequest;
import io.playqd.client.PlayqdClient;
import io.playqd.data.Album;
import io.playqd.data.Artist;
import io.playqd.data.Track;
import io.playqd.event.MouseEventHelper;
import io.playqd.player.AlbumGridViewActionListener;
import io.playqd.player.PlayRequest;
import io.playqd.player.PlayerEngine;
import io.playqd.player.PlayingQueue;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import org.controlsfx.control.GridView;

import java.util.ArrayList;
import java.util.List;

public class MusicSplitPaneController {

    private PlayqdClient playqdClient = PlayqdClient.builder().apiBaseUrl("http://gkos-srv:8017/api/v1").build();

    @FXML
    private ListView<Artist> artistsListView;

    @FXML
    private GridView<Album> albumsGridView;

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

        var artists = playqdClient.getArtists(GetArtistsRequest.createDefault(), PageRequest.defaultPage());

        var allArtistsArtist = new Artist("all-artists", "All Artists", artists.content().size(), -1);

        var result = new ArrayList<Artist>(artists.content().size() + 1);
        result.add(allArtistsArtist);
        result.addAll(artists.content());

        artistsListView.setItems(FXCollections.observableArrayList(result));


        artistsListView.getSelectionModel().selectedItemProperty().addListener((_, _, selectedArtist) -> {
            albumsGridView.setCellFactory(new AlbumsGridViewCellFactory(new AlbumGridViewActionListenerImpl()));
            var albums = playqdClient.getAlbumsByArtistId(selectedArtist.id());
            var tracks = playqdClient.getTracksByArtistId(selectedArtist.id());
            albumsGridView.setItems(FXCollections.observableArrayList(albums.content()));

            tracksTableView.getItems().addAll(tracks.content());
            tracksTableView.setUserData(tracks.content());
        });

        trackNumberCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().number()));
        titleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().title()));
        timeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().length().readable()));
    }

    private class AlbumGridViewActionListenerImpl implements AlbumGridViewActionListener {

        @Override
        public void onAlbumImageClicked(Album album) {
            tracksTableView.getItems().clear();
            tracksTableView.getItems().addAll(getAlbumTracks(album));
        }

        @Override
        public void onAlbumImageDoubleClicked(Album album) {
            PlayerEngine.enqueueAndPlay(new PlayRequest(tracksTableView.getItems()));
        }

        private List<Track> getAlbumTracks(Album album) {

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
    }
}
