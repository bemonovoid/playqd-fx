package io.playqd.controller;

import io.playqd.client.GetSearchResponse;
import io.playqd.client.PageRequest;
import io.playqd.data.SearchFlag;
import io.playqd.data.SearchRequestParams;
import io.playqd.data.Track;
import io.playqd.event.PlayqdEvent;
import io.playqd.service.SearchEngine;
import io.playqd.service.SearchEngineImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.control.textfield.TextFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SearchVBoxController {

    private static final Logger LOG = LoggerFactory.getLogger(SearchVBoxController.class);

    private TextField searchTextField;

    private ObservableSet<SearchFlag> searchFlags = FXCollections.observableSet(new HashSet<>());

    @FXML
    private HBox searchTextFieldHBox;

    @FXML
    private ComboBox<String> pageSizeComboBox;

    @FXML
    private TabPane tabPane;

    @FXML
    private ToggleSwitch searchFilenameSwitch;

    @FXML
    private Tab tracksTab;

    @FXML
    private TableView<Track> tracksTableView;

    @FXML
    private TableColumn<Track, String> titleCol, artistCol, albumCol, filenameCol, lengthCol, genreCol, sizeCol, extensionCol, bitRateCol, sampleRateCol, bitsPerSampleCol, ratingCol, mimeTypeCol, playCountCol, lastPlayedDateCol, addedDateCol;

    @FXML
    private Pagination trackPagination;

    private SearchEngine searchEngine;

    @FXML
    private void initialize() {
        initTable();
        initControls();
        initSearchFlags();
        this.searchEngine = new SearchEngineImpl();
    }

    private void initTable() {
        tracksTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tracksTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        setTableCellValueFactories();
//        treeTableView.setPlaceholder(createPlaceholder());
    }

    private void setTableCellValueFactories() {
        titleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().title()));
        artistCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().artist().name()));
        albumCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().album().name()));
        genreCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().album().genre()));
        filenameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fileAttributes().name()));
        lengthCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().length().readable()));
        sizeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fileAttributes().size()));
        extensionCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fileAttributes().extension()));
        mimeTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().audioFormat().mimeType()));
        bitRateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().audioFormat().bitRate()));
        sampleRateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().audioFormat().sampleRate()));
        bitsPerSampleCol.setCellValueFactory(c -> new SimpleStringProperty("" + c.getValue().audioFormat().bitsPerSample()));
        ratingCol.setCellValueFactory(c -> new SimpleStringProperty("" + c.getValue().rating().value()));
        playCountCol.setCellValueFactory(c -> new SimpleStringProperty("" + c.getValue().playback().count()));
        lastPlayedDateCol.setCellValueFactory(c -> {
            if (c.getValue().playback().lastPlayedDate() != null) {
                var date = c.getValue().playback().lastPlayedDate();
                return new SimpleStringProperty(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
            return new SimpleStringProperty("");
        });
        addedDateCol.setCellValueFactory(c -> {
            if (c.getValue().additionalInfo().addedToWatchFolderDate() != null) {
                var date = c.getValue().additionalInfo().addedToWatchFolderDate();
                return new SimpleStringProperty(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
            return new SimpleStringProperty("");
        });

    }

    private void initControls() {
        this.searchTextField = TextFields.createClearableTextField();
        this.searchTextField.setPromptText("↵  Search ...");

        this.searchTextField.setOnKeyPressed(keyEvent -> {
            if (KeyCode.ENTER == keyEvent.getCode()) {
                var query = this.searchTextField.getText();
                if (query == null || query.isEmpty() || query.trim().isEmpty()) {
                    LOG.warn("Empty search text.");
                } else {
                    setItems(search());
                }
                keyEvent.consume();
            }
        });

        searchTextFieldHBox.getChildren().add(this.searchTextField);

        pageSizeComboBox.setOnAction(_ -> search(getPageSize()));
        trackPagination.currentPageIndexProperty().addListener((_, _, newIdx) -> {
            setItems(search(newIdx.intValue()));
        });

    }

    private void initSearchFlags() {
        searchFlags.addListener((SetChangeListener<? super SearchFlag>) searchFlagListener -> {
            if (searchFlagListener.wasAdded() || searchFlagListener.wasRemoved()) {
                search();
            }
        });
    }

    private GetSearchResponse search() {
        return search(0);
    }

    private GetSearchResponse search(int pageIdx) {
        var searchRequest = new SearchRequestParams();
        searchRequest.setInput(searchTextField.getText().isEmpty() ? "" : "contains:" + searchTextField.getText());
        searchRequest.setSearchFlags(Collections.unmodifiableSet(searchFlags));
        var pageRequest = new PageRequest(pageIdx, getPageSize());
        return searchEngine.search(searchRequest, pageRequest);
    }

    private void setItems(GetSearchResponse response) {
        if (!response.tracks().isEmpty()) {
            tracksTableView.setItems(FXCollections.observableList(response.tracks().content()));
            var tracksPage = response.tracks().page();
            tracksTab.setText("Tracks (" + tracksPage.totalElements() + ")");
            trackPagination.setPageCount(tracksPage.totalPages());
            trackPagination.setCurrentPageIndex(tracksPage.number());
        }
    }

    private int getPageSize() {
        var value = this.pageSizeComboBox.getSelectionModel().getSelectedItem();
        if (value == null) {
            return 50;
        }
        if (value.equalsIgnoreCase("all")) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    void onSearchFlagChangedEvent(PlayqdEvent.SearchFlagChangedEvent event) {
        switch (event.searchFlag()) {
            case TEXT_STARTS_WITH -> {
            }
            case TEXT_CONTAINS -> {
            }
            case PLAYED -> {
            }
            case RATED -> {
            }
            case SEARCH_IN_ARTISTS -> {
            }
            case SEARCH_IN_ALBUMS -> {
            }
            case SEARCH_IN_GENRES -> {
            }
            case SEARCH_IN_TRACKS -> {
            }
            case SEARCH_BY_LYRICS -> {
            }
            case SEARCH_BY_COMMENT -> {
            }
            case SEARCH_BY_FILE_NAME -> {
            }
            case NONE -> {
            }
            case null, default -> {
                if (event.searchFlag() != null) {
                    if (event.enabled()) {
                        searchFlags.add(event.searchFlag());
                    } else {
                        searchFlags.remove(event.searchFlag());
                    }
                }
            }
        }
    }
}
