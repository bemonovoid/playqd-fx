package io.playqd.controller.music;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import io.playqd.client.PlayqdClientProvider;
import io.playqd.data.Artist;
import io.playqd.service.TracksService;
import io.playqd.utils.FakeIds;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MusicLibraryArtistsController extends MusicSplitPaneController {

    private static final Logger LOG = LoggerFactory.getLogger(MusicLibraryArtistsController.class);

    private final Searchable searchable = new SearchTextController();

    @FXML
    private Label artistsSearchLabel;

    @FXML
    private MenuButton sortArtistsMenuBtn;

    @FXML
    private Label artistsInfoLabel;

    protected void initializeInternal() {
        super.initializeInternal();
        initSortMenuButton();

        artistsListView.getStyleClass().add("artists-list-view");

        searchable.initialize(artistsListView, onSearchTextInputChanged(), onSearchTextInputCleared());

        artistsListView.getSelectionModel().selectedItemProperty().addListener((_, oldArtist, selectedArtist) -> {
            if (oldArtist != null && selectedArtist == null) {
                clearAlbumsList();
                getTracksContainer().clearTracksTable();
            }
            if (selectedArtist != null) {
                getAlbumsListView().setCellFactory(
                        new AlbumsListViewCellFactory(new AlbumsListViewCellFactoryListener(this)));
                if (FakeIds.ALL_ARTIST == selectedArtist.id()) {
                    showAllAlbums();
                    tracksContainer.showTracks(TracksService::getAllTracks);
                } else {
                    showArtistAlbums(selectedArtist);
                }
            }
        });

        initArtistsInfoLabelListener();

        showAllArtists();
    }

    private void initSortMenuButton() {
        sortArtistsMenuBtn.getStyleClass().setAll("button");
        var byNameAscMenuItem = new CustomMenuItem(FontAwesomeIconFactory.get().createIconLabel(
                FontAwesomeIcon.SORT_ALPHA_ASC, "Name ascending", "10px", "12px", ContentDisplay.LEFT));
        var byNameDescMenuItem = new CustomMenuItem(FontAwesomeIconFactory.get().createIconLabel(
                FontAwesomeIcon.SORT_ALPHA_DESC, "Name descending", "10px", "12px", ContentDisplay.LEFT));
        var byAlbumCountAscMenuItem = new CustomMenuItem(FontAwesomeIconFactory.get().createIconLabel(
                FontAwesomeIcon.SORT_AMOUNT_ASC, "Albums count ascending", "10px", "12px", ContentDisplay.LEFT));
        var byAlbumCountDescMenuItem = new CustomMenuItem(FontAwesomeIconFactory.get().createIconLabel(
                FontAwesomeIcon.SORT_AMOUNT_DESC, "Albums count descending", "10px", "12px", ContentDisplay.LEFT));
        var byTracksCountAscMenuItem = new CustomMenuItem(FontAwesomeIconFactory.get().createIconLabel(
                FontAwesomeIcon.SORT_AMOUNT_ASC, "Tracks count ascending", "10px", "12px", ContentDisplay.LEFT));
        var byTracksCountDescMenuItem = new CustomMenuItem(FontAwesomeIconFactory.get().createIconLabel(
                FontAwesomeIcon.SORT_AMOUNT_DESC, "Tracks count descending", "10px", "12px", ContentDisplay.LEFT));

        byNameAscMenuItem.setOnAction(_ -> sort(Comparator.comparing(Artist::name), false));
        byNameDescMenuItem.setOnAction(_ -> sort(Comparator.comparing(Artist::name), true));

        byAlbumCountAscMenuItem.setOnAction(_ -> sort(Comparator.comparing(Artist::albumsCount), false));
        byAlbumCountDescMenuItem.setOnAction(_ -> sort(Comparator.comparing(Artist::albumsCount), true));

        byTracksCountAscMenuItem.setOnAction(_ -> sort(Comparator.comparing(Artist::tracksCount), false));
        byTracksCountDescMenuItem.setOnAction(_ -> sort(Comparator.comparing(Artist::tracksCount), true));

        sortArtistsMenuBtn.getItems().addAll(byNameAscMenuItem, byNameDescMenuItem, new SeparatorMenuItem(),
                byAlbumCountAscMenuItem, byAlbumCountDescMenuItem, new SeparatorMenuItem(),
                byTracksCountAscMenuItem, byTracksCountDescMenuItem);
    }

    private void initArtistsInfoLabelListener() {
        artistsInfoLabel.setDisable(true);
        artistsInfoLabel.setStyle("-fx-font-size: 11px;");
        artistsInfoLabel.setOpacity(0.6);
        artistsListView.itemsProperty().addListener((_, _, newItems) -> {
            if (newItems == null || newItems.isEmpty()) {
                artistsInfoLabel.setText("");
            } else {
                var artistsText = newItems.size() > 1 ? "artists" : "artist";
                artistsInfoLabel.setText(newItems.size() + " " + artistsText);
            }
        });
    }

    private void sort(Comparator<Artist> comparator, boolean reversed) {
        var allArtistsItem = artistsListView.getItems().getFirst();
        var artistItems = artistsListView.getItems().subList(1, artistsListView.getItems().size());
        if (reversed) {
            artistItems.sort(comparator.reversed());
        } else {
            artistItems.sort(comparator);
        }
        var sortedItems = new ArrayList<Artist>(artistsListView.getItems().size());
        sortedItems.add(allArtistsItem);
        sortedItems.addAll(artistItems);
        artistsListView.setItems(FXCollections.observableArrayList(sortedItems));
    }

    private Consumer<String> onSearchTextInputChanged() {
        return newInput -> {
            artistsListView.getSelectionModel().clearSelection();
            if (newInput.isEmpty()) {
                artistsSearchLabel.setText("");
                artistsSearchLabel.setVisible(false);
                return;
            }
            @SuppressWarnings("unchecked")
            var items = new ArrayList<>((List<Artist>) artistsListView.getUserData()).stream()
                    .filter(artist -> artist.name().toLowerCase().contains(newInput))
                    .filter(artist -> FakeIds.ALL_ARTIST != artist.id())
                    .collect(Collectors.toCollection(ArrayList::new));
            LOG.info("Search by: '{}'. Found: {}", newInput, items.size());
            if (items.isEmpty()) {
                artistsListView.getItems().clear();
            } else {
                items.sort(Comparator.comparing(a -> a.name().toLowerCase()));
                artistsListView.setItems(FXCollections.observableArrayList(items));
                artistsListView.getSelectionModel().selectFirst();
            }
            if (!artistsSearchLabel.isVisible()) {
                artistsSearchLabel.setVisible(true);
            }
            artistsSearchLabel.setText(newInput);
        };
    }

    private Runnable onSearchTextInputCleared() {
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
