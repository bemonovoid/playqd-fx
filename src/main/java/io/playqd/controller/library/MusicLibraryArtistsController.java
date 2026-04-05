package io.playqd.controller.library;

import io.playqd.controller.view.ApplicationViews;
import io.playqd.controller.view.ObservableProperties;
import io.playqd.data.Artist;
import io.playqd.service.MusicLibrary;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
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
    private VBox azVBox;

    @FXML
    private Label artistsInfoLabel;

    protected void initializeInternal() {
        super.initializeInternal();
        initToggleGroupsListeners();
        buildAzColumn();
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
        onViewRequestListener();

        MusicLibrary.libraryRefreshedEventProperty().addListener((_, _, _) -> showAllArtists());

        showAllArtists();
    }

    private void buildAzColumn() {
        var children = azVBox.getChildren();
        var all = new Hyperlink("All");
        all.setStyle("-fx-font-size: 10");
        all.setOnAction(_ -> filterByCharGroup(KeyCode.ESCAPE));
        children.add(all);
        for (char c = 'A'; c <= 'Z'; c++) {
            var str = Character.toString(c);
            var hl = new Hyperlink(str);
            hl.setStyle("-fx-font-size: 10");
            hl.setOnAction(_ -> filterByCharGroup(KeyCode.valueOf(str)));
            children.add(hl);
        }
        var numbers = new Hyperlink("0-9");
        numbers.setStyle("-fx-font-size: 10");
        numbers.setOnAction(_ -> filterByCharGroup(KeyCode.DIGIT1));
        var misc = new Hyperlink("!#?");
        misc.setStyle("-fx-font-size: 10");
        misc.setOnAction(_ -> filterByCharGroup(KeyCode.STAR));
        children.addAll(numbers, misc);
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

    private Consumer<String> onSearchTextInputChanged() {
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

    private void onViewRequestListener() {
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
}
