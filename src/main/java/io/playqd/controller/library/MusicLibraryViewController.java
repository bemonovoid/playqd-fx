package io.playqd.controller.library;

import io.playqd.controller.search.SearchTextController;
import io.playqd.controller.search.Searchable;
import io.playqd.controller.view.ApplicationViews;
import io.playqd.controller.view.ObservableProperties;
import io.playqd.controller.view.TracksView;
import io.playqd.data.Album;
import io.playqd.data.Artist;
import io.playqd.data.Track;
import io.playqd.player.PlayerTrackListManager;
import io.playqd.player.TrackListRequest;
import io.playqd.service.MusicLibrary;
import io.playqd.service.TrackComparators;
import io.playqd.utils.FakeIds;
import io.playqd.utils.Numbers;
import io.playqd.utils.SortDirection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MusicLibraryViewController {

    private static final Logger LOG = LoggerFactory.getLogger(MusicLibraryViewController.class);

    private final Searchable artistSearchable = new SearchTextController();

    private final Searchable albumSearchable = new SearchTextController();

    @FXML
    private VBox azVBox;

    @FXML
    private Label artistsSearchLabel, albumsSearchLabel, artistsInfoLabel, albumsInfoLabel;

    @FXML
    private ListView<Artist> artistsListView;

    @FXML
    private ListView<Album> albumsListView;

    @FXML
    protected TracksView tracksView;

    @FXML
    private ToggleGroup sortArtistsByToggleGroup, sortArtistsDirectionToggleGroup;

    @FXML
    private MenuItem sortArtistsByName, sortArtistsByAlbCount, sortArtistsByTracksCount, sortArtistsAsc, sortArtistsDesc;

    @FXML
    private void initialize() {
        initArtistsView();
        initAlbumsView();
        initTracksView();
        tracksView().tracksTableView().rowDoubleClickedProperty().addListener((_, _, row) -> {
            if (row != null) {
                var trackListReq = new TrackListRequest(row.index(), tracksView().tracksTableView().getItemsAsTracks());
                PlayerTrackListManager.enqueueAndPlay(trackListReq);
            }
        });


        onViewRequestedExternally();
        MusicLibrary.libraryRefreshedEventProperty().addListener((_, _, _) -> showAllArtists());

    }

    private void initArtistsView() {
        initToggleGroupsListeners();
        AzItemsBuilder.build(azVBox, this::filterByCharGroup);
        artistsListView.getStyleClass().add("artists-list-view");

        artistSearchable.initialize(artistsListView, onArtistSearchTextInputChanged(), onArtistSearchTextInputCleared());

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

    private void initAlbumsView() {
        albumSearchable.initialize(albumsListView, onSearchTextInputChanged(), onSearchTextInputCleared());
        albumsListView.getSelectionModel().selectedItemProperty().addListener((_, oldAlbum, selectedAlbum) -> {
            if (oldAlbum != null && selectedAlbum == null) {
                tracksView().clear();
            }
            if (selectedAlbum != null) { // is null when some album is selected but next selection is artist list view
                tracksView().tracksTableView().showTracks(
                        () -> MusicLibrary.getAlbumTracks(selectedAlbum.id()),
                        TrackComparators.trackInstanceNumberComparator());
            }
        });
        initAlbumsInfoLabelListener();
    }

    private void initTracksView() {
        tracksView.tracksTableView().getColumns().stream()
                .filter(col ->
                        col != tracksView.tracksTableView().artworkCol &&
                                col != tracksView.tracksTableView().trackNumberCol &&
                                col != tracksView.tracksTableView().titleCol &&
                                col != tracksView.tracksTableView().timeCol)
                .forEach(col -> col.setVisible(false));
        tracksView.tracksTableView().timeCol.setMinWidth(60);
        tracksView.tracksTableView().timeCol.setMaxWidth(60);
    }

    private void filterByCharGroup(KeyCode keyCode) {
        if (KeyCode.ESCAPE == keyCode) {
            onSearchTextInputCleared().run();
        } else if (KeyCode.STAR == keyCode) {
            applyArtistNameContainsNonStandardCharacter();
        } else if (keyCode.isLetterKey()) {
            applyArtistNameStartsWithFilter(keyCode.getChar());
        } else if (keyCode.isDigitKey()) {
            applyArtistNameContainsDigitFilter();
        }
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
            var itemsSize = newItems == null ? 0 : newItems.size() - 1; // exclude 'All artists items'
            if (itemsSize == 0) {
                artistsInfoLabel.setText("");
            } else {
                var artistsText = itemsSize > 1 ? "artists" : "artist";
                artistsInfoLabel.setText(Numbers.format(itemsSize) + " " + artistsText);
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

    private Consumer<String> onArtistSearchTextInputChanged() {
        return newInput -> {
            artistsListView.getSelectionModel().clearSelection();
            if (newInput.isEmpty()) {
                artistsSearchLabel.setText("");
                artistsSearchLabel.setVisible(false);
                return;
            }

            applyArtistNameContainsFilter(newInput);

            if (!artistsSearchLabel.isVisible()) {
                artistsSearchLabel.setVisible(true);
            }
            artistsSearchLabel.setText(newInput);
        };
    }

    private void applyArtistNameContainsFilter(String input) {
        var result = applyArtistNameFilter(artist -> artist.name().toLowerCase().contains(input));
        LOG.info("Input: '{}'. Found: {}", input, result);
    }

    private void applyArtistNameStartsWithFilter(String input) {
        var result = applyArtistNameFilter(artist -> artist.name().toLowerCase().startsWith(input.toLowerCase()));
        LOG.info("Input: '{}'. Found: {}", input, result);
    }

    private void applyArtistNameContainsDigitFilter() {
        var result = applyArtistNameFilter(artist -> {
            for (char c : artist.name().toCharArray()) {
                if (Character.isDigit(c)) {
                    return true;
                }
            }
            return false;
        });
        LOG.info("Input: 'contains digit'. Found: {}", result);
    }

    private void applyArtistNameContainsNonStandardCharacter() {
        var pattern = Pattern.compile("^[^a-zA-Z0-9\\s]+$");
        var result = applyArtistNameFilter(artist -> pattern.matcher(artist.name()).find());
        LOG.info("Input: 'non standard char'. Found: {}", result);
    }

    private int applyArtistNameFilter(Predicate<Artist> predicate) {
        @SuppressWarnings("unchecked")
        var items = new ArrayList<>((List<Artist>) artistsListView.getUserData()).stream()
                .filter(predicate)
                .filter(artist -> FakeIds.ALL_ARTIST != artist.id())
                .collect(Collectors.toCollection(ArrayList::new));
        if (items.isEmpty()) {
            artistsListView.getItems().clear();
        } else {
            items.sort(Comparator.comparing(a -> a.name().toLowerCase()));
            artistsListView.setItems(FXCollections.observableArrayList(items));
            artistsListView.getSelectionModel().selectFirst();
        }
        return items.size();
    }

    private Runnable onArtistSearchTextInputCleared() {
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

    private void onViewRequestedExternally() {
        ObservableProperties.getAppViewRequestProperty().addListener((_, _, newValue) -> {
            if (newValue != null && ApplicationViews.MUSIC_LIBRARY == newValue.view()) {
                var viewReq = newValue.musicLibraryViewRequest();
                for (int i = 0; i < artistsListView.getItems().size(); i++) {
                    if (artistsListView.getItems().get(i).name().equals(viewReq.track().artistName())) {
                        artistsListView.scrollTo(i);
                        artistsListView.getSelectionModel().select(i);
                    }
                }
            }
        });
    }

    static Artist createFakeAllArtistsArtist(int totalArtists) {
        return new Artist(FakeIds.ALL_ARTIST, "All Artists", totalArtists, -1);
    }

    void showAllAlbums() {
        Platform.runLater(() -> {
            var albums = MusicLibrary.getAllAlbums();
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
            var albums = MusicLibrary.getArtistAlbums(selectedArtist.id());
            var albumsListViewItems = new ArrayList<Album>(albums.size() + 1);
            var headerAlbum = createFakeArtistAlbumsAlbum(albums.getFirst());
            albumsListViewItems.add(headerAlbum);
            albumsListViewItems.addAll(albums);
            albumsListView.setItems(FXCollections.observableArrayList(albumsListViewItems));
            tracksView.tracksTableView().showTracks(() -> getArtistTracks(selectedArtist.id()));
        });
    }

    List<Track> getArtistTracks(long trackId) {
        return MusicLibrary.getArtistTracks(trackId).stream()
                .sorted(TrackComparators.byAlbumAndTrackNumber())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    void clearAlbumsList() {
        albumsListView.getItems().clear();
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
                albumsInfoLabel.setText(Numbers.format(newItems.size()) + " " + albumsText);
            }
        });
    }

    protected final Artist getSelectedArtist() {
        return artistsListView.getSelectionModel().getSelectedItem();
    }

    protected final ListView<Album> getAlbumsListView() {
        return albumsListView;
    }

    protected final TracksView tracksView() {
        return tracksView;
    }

    protected final Album getSelectedAlbum() {
        return getAlbumsListView().getSelectionModel().getSelectedItem();
    }

    static Album createFakeArtistAlbumsAlbum(Album artistAlbum) {
        return new Album(
                artistAlbum.id(), FakeIds.ALL_ARTIST_ALBUMS_NAME, "", "", artistAlbum.artistName(), null, 0, 0);
    }
}
