package io.playqd.controller;

import io.playqd.client.GetSearchResponse;
import io.playqd.data.SearchFlag;
import io.playqd.data.SearchRequestParams;
import io.playqd.service.SearchEngineImpl;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import org.controlsfx.control.textfield.TextFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SearchVBoxController extends AbstractSearchController {

    private static final Logger LOG = LoggerFactory.getLogger(SearchVBoxController.class);

    private TextField searchTextField;

    private final ObservableSet<SearchFlag> searchFlags = FXCollections.observableSet(new HashSet<>());

    @FXML
    private SearchResultTabPaneController searchResultTabPaneController;

    @FXML
    private HBox searchTextFieldHBox;

    @FXML
    private ComboBox<String> pageSizeComboBox;

    @FXML
    private RadioButton containsRadioBtn, startsWithRadioBtn;

    @FXML
    private void initialize() {
        super.initializeInternal();
        initControls();
        initSearchFlags();
        this.searchEngine = new SearchEngineImpl();
    }

    private void initControls() {
        var toggleGroup = new ToggleGroup();
        containsRadioBtn.setToggleGroup(toggleGroup);
        containsRadioBtn.selectedProperty().addListener((_, _, selected) -> {
            if (selected) {
                searchFlags.remove(SearchFlag.TEXT_STARTS_WITH);
                searchFlags.add(SearchFlag.TEXT_CONTAINS);
            }
        });
        startsWithRadioBtn.setToggleGroup(toggleGroup);
        startsWithRadioBtn.selectedProperty().addListener((_, _, selected) -> {
            if (selected) {
                searchFlags.remove(SearchFlag.TEXT_CONTAINS);
                searchFlags.add(SearchFlag.TEXT_STARTS_WITH);
            }
        });

        this.searchTextField = TextFields.createClearableTextField();
        this.searchTextField.setPrefWidth(250);
        this.searchTextField.setPromptText("↵  Search ...");
        this.searchTextField.setOnKeyPressed(keyEvent -> {
            if (KeyCode.ENTER == keyEvent.getCode()) {
                var query = this.searchTextField.getText();
                if (query == null || query.isEmpty() || query.trim().isEmpty()) {
                    LOG.warn("Empty search text.");
                } else {
                    searchResultTabPaneController.setItems(search());
                }
                keyEvent.consume();
            }
        });
        searchTextFieldHBox.getChildren().add(this.searchTextField);

        pageSizeComboBox.setOnAction(_ -> search());
    }

    private GetSearchResponse search() {
        return search(getSearchRequestParams(), 0, getPageSize());
    }

    private void initSearchFlags() {
        searchFlags.addAll(Set.of(
                SearchFlag.SEARCH_IN_TRACKS, SearchFlag.SEARCH_BY_FILE_NAME));
        if (containsRadioBtn.isSelected()) {
            searchFlags.add(SearchFlag.TEXT_CONTAINS);
        } else if (startsWithRadioBtn.isSelected()) {
            searchFlags.add(SearchFlag.TEXT_STARTS_WITH);
        }
    }

    @Override
    protected final SearchRequestParams getSearchRequestParams() {
        var searchRequest = new SearchRequestParams();
        searchRequest.setInput(searchTextField.getText().isEmpty() ? "" : searchTextField.getText());
        searchRequest.setSearchFlags(Collections.unmodifiableSet(searchFlags));
        return searchRequest;
    }

    @Override
    protected final int getPageSize() {
        var value = this.pageSizeComboBox.getSelectionModel().getSelectedItem();
        if (value == null) {
            return 50;
        }
        if (value.equalsIgnoreCase("all")) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    void bindFilterControls(SearchFiltersVBoxController searchFiltersController) {
        searchFiltersController.searchFilenameSwitch.setSelected(searchFlags.contains(SearchFlag.SEARCH_BY_FILE_NAME));
        if (searchFlags.contains(SearchFlag.SEARCH_IN_TRACKS)) {
            searchFiltersController.searchInComboBox.getCheckModel().check(3);
        }
        searchFiltersController.searchFilenameSwitch.selectedProperty().addListener((_, _, selected) -> {
            if (selected) {
                searchFlags.add(SearchFlag.SEARCH_BY_FILE_NAME);
            } else {
                searchFlags.remove(SearchFlag.SEARCH_BY_FILE_NAME);
            }
        });
        searchFiltersController.searchInComboBox.getCheckModel().getCheckedItems()
                .addListener((ListChangeListener<? super String>) change -> {
                    change.next();
                    if (change.wasAdded()) {
                        change.getAddedSubList().forEach(searchInItem -> {
                            if (searchInItem.equals("Artists")) {
                                searchFlags.add(SearchFlag.SEARCH_IN_ARTISTS);
                            } else if (searchInItem.equals("Albums")) {
                                searchFlags.add(SearchFlag.SEARCH_IN_ALBUMS);
                            } else if (searchInItem.equals("Genres")) {
                                searchFlags.add(SearchFlag.SEARCH_IN_GENRES);
                            } else if (searchInItem.equals("Tracks")) {
                                searchFlags.add(SearchFlag.SEARCH_IN_TRACKS);
                            }
                        });
                    } else if (change.wasRemoved()) {
                        change.getRemoved().forEach(searchInItem -> {
                            if (searchInItem.equals("Artists")) {
                                searchFlags.remove(SearchFlag.SEARCH_IN_ARTISTS);
                            } else if (searchInItem.equals("Albums")) {
                                searchFlags.remove(SearchFlag.SEARCH_IN_ALBUMS);
                            } else if (searchInItem.equals("Genres")) {
                                searchFlags.remove(SearchFlag.SEARCH_IN_GENRES);
                            } else if (searchInItem.equals("Tracks")) {
                                searchFlags.remove(SearchFlag.SEARCH_IN_TRACKS);
                            }
                        });
                    }
                });
    }
}
