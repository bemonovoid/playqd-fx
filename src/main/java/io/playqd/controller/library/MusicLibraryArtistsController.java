package io.playqd.controller.library;

import io.playqd.data.Artist;
import io.playqd.service.MusicLibrary;
import io.playqd.utils.FakeIds;
import io.playqd.utils.Numbers;
import io.playqd.utils.SortDirection;
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
    private ToggleGroup sortArtistsByToggleGroup, sortArtistsDirectionToggleGroup;

    @FXML
    private MenuItem sortArtistsByName, sortArtistsByAlbCount, sortArtistsByTracksCount, sortArtistsAsc, sortArtistsDesc;

    @FXML
    private Label artistsInfoLabel;

    protected void initializeInternal() {
        super.initializeInternal();
        initToggleGroupsListeners();

        artistsListView.getStyleClass().add("artists-list-view");

        searchable.initialize(artistsListView, onSearchTextInputChanged(), onSearchTextInputCleared());

        artistsListView.getSelectionModel().selectedItemProperty().addListener((_, oldArtist, selectedArtist) -> {
            if (oldArtist != null && selectedArtist == null) {
                clearAlbumsList();
                tracksView().clear();
            }
            if (selectedArtist != null) {
                getAlbumsListView().setCellFactory(
                        new AlbumsListViewCellFactory(new AlbumsListViewCellFactoryListener(this)));
                if (FakeIds.ALL_ARTIST == selectedArtist.id()) {
                    showAllAlbums();
                    tracksView().tracksTableView().showTracks(MusicLibrary::getAllTracksExcludingCueParent);
                } else {
                    showArtistAlbums(selectedArtist);
                }
            }
        });

        initArtistsInfoLabelListener();

        showAllArtists();
    }

    private void initToggleGroupsListeners() {
        sortArtistsByToggleGroup.selectedToggleProperty().addListener((_, _, newToggle) ->
                sortArtists(newToggle, getSelectedSortDirection()));
        sortArtistsDirectionToggleGroup.selectedToggleProperty().addListener((_, _, newToggle) ->
                sortArtists(sortArtistsByToggleGroup.getSelectedToggle(), resolveSortDirectionFromToggle(newToggle)));
    }

    private SortDirection getSelectedSortDirection() {
        var selectedToggle = sortArtistsDirectionToggleGroup.getSelectedToggle();
        return resolveSortDirectionFromToggle(selectedToggle);
    }

    private SortDirection resolveSortDirectionFromToggle(Toggle toggle) {
        if (toggle == null || toggle == sortArtistsAsc) {
            return SortDirection.ASC;
        }
        return SortDirection.DESC;
    }

    private void sortArtists(Toggle sortBy, SortDirection sortDirection) {
        var sortByToggle = sortBy == null ? sortArtistsByName : sortBy;
        if (sortByToggle == sortArtistsByName) {
            sort(Comparator.comparing(Artist::name), sortDirection);
        } else if (sortByToggle == sortArtistsByAlbCount) {
            sort(Comparator.comparing(Artist::albumsCount), sortDirection);
        } else if (sortByToggle == sortArtistsByTracksCount) {
            sort(Comparator.comparing(Artist::tracksCount), sortDirection);
        }
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
                artistsInfoLabel.setText(Numbers.format(newItems.size()) + " " + artistsText);
            }
        });
    }

    private void sort(Comparator<Artist> comparator, SortDirection sortDirection) {
        var allArtistsItem = artistsListView.getItems().getFirst();
        var artistItems = artistsListView.getItems().subList(1, artistsListView.getItems().size());
        if (SortDirection.DESC == sortDirection) {
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
            var artists = MusicLibrary.getArtists().stream()
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
