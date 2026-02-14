package io.playqd.controller;

import io.playqd.data.SearchFlag;
import io.playqd.data.SearchRequestParams;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.textfield.TextFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchSplitPaneController extends AbstractSearchController {

    private static final Logger LOG = LoggerFactory.getLogger(SearchSplitPaneController.class);

    protected TextField searchTextField;

    @FXML
    protected SearchResultTabPaneController searchResultTabPaneController;

    @FXML
    protected VBox searchVBox;

    @FXML
    protected HBox searchTextFieldHBox;

    @FXML
    private RadioButton searchInNamesRadioBtn, searchInCommentsRadioBtn, searchInLyricsRadioBtn, containsRadioBtn, startsWithRadioBtn;

    @FXML
    CheckComboBox<String> albumsByGenreCheckComboBox;

    @FXML
    private CheckBox searchFilenameCheckBox, commentsExistCheckBox, lyricsExistCheckBox;

    @FXML
    protected ComboBox<String> pageSizeComboBox;

    @FXML
    private void initialize() {
        initControls();
        setPlayqdEventHandlers();
    }

    private void initControls() {
        initTextSearchGroupControls();
        initTracksSearchGroupControls();

        pageSizeComboBox.setOnAction(_ -> search());
    }

    private void initTextSearchGroupControls() {
        initSearchInToggleGroup();
        initSearchInputTextField();

        if (containsRadioBtn.isSelected()) {
            searchFlags.add(SearchFlag.TEXT_CONTAINS);
        } else if (startsWithRadioBtn.isSelected()) {
            searchFlags.add(SearchFlag.TEXT_STARTS_WITH);
        }

        initSearchTextToggleGroup();
    }

    private void initSearchInputTextField() {
        this.searchTextField = TextFields.createClearableTextField();
        this.searchTextField.setText("");
        this.searchTextField.setPrefWidth(250);
        this.searchTextField.setPromptText("↵  Search ...");
        this.searchTextField.setOnKeyPressed(keyEvent -> {
            if (KeyCode.ENTER == keyEvent.getCode()) {
                var query = this.searchTextField.getText();
                if (query == null || query.isEmpty() || query.trim().isEmpty()) {
                    LOG.warn("Empty search text.");
                } else {
                    search();
                }
                keyEvent.consume();
            }
        });
        searchTextFieldHBox.getChildren().add(this.searchTextField);
    }

    private void initSearchInToggleGroup() {
        var searchInToggleGroup = new ToggleGroup();
        searchInNamesRadioBtn.setToggleGroup(searchInToggleGroup);
        searchInCommentsRadioBtn.setToggleGroup(searchInToggleGroup);
        searchInLyricsRadioBtn.setToggleGroup(searchInToggleGroup);

        searchInToggleGroup.selectedToggleProperty().addListener((_, _, change) -> {
            if (change instanceof RadioButton r) {
                var search = false;
                if (r.getId().equalsIgnoreCase(searchInNamesRadioBtn.getId())) {
                    searchFlags.remove(SearchFlag.SEARCH_BY_COMMENT);
                    searchFlags.remove(SearchFlag.SEARCH_BY_LYRICS);
                } else if (r.getId().equalsIgnoreCase(searchInCommentsRadioBtn.getId())) {
                    searchFlags.remove(SearchFlag.SEARCH_BY_LYRICS);
                    searchFlags.add(SearchFlag.SEARCH_BY_COMMENT);
                    search = true;
                } else if (r.getId().equalsIgnoreCase(searchInLyricsRadioBtn.getId())) {
                    searchFlags.remove(SearchFlag.SEARCH_BY_COMMENT);
                    searchFlags.add(SearchFlag.SEARCH_BY_LYRICS);
                    search = true;
                }
                if (hasSearchText() && search) {
                    search();
                }
            }
        });
    }

    private void initSearchTextToggleGroup() {
        var textSearchToggleGroup = new ToggleGroup();
        containsRadioBtn.setToggleGroup(textSearchToggleGroup);
        startsWithRadioBtn.setToggleGroup(textSearchToggleGroup);

        textSearchToggleGroup.selectedToggleProperty().addListener((_, _, change) -> {
            if (change instanceof RadioButton r) {
                var search = false;
                if (r.getId().equalsIgnoreCase(containsRadioBtn.getId())) {
                    searchFlags.remove(SearchFlag.TEXT_STARTS_WITH);
                    searchFlags.add(SearchFlag.TEXT_CONTAINS);
                    search = true;
                } else if (r.getId().equalsIgnoreCase(startsWithRadioBtn.getId())) {
                    searchFlags.remove(SearchFlag.TEXT_CONTAINS);
                    searchFlags.add(SearchFlag.TEXT_STARTS_WITH);
                    search = true;
                }
                if (hasSearchText() && search) {
                    search();
                }
            }
        });
    }

    private void initTracksSearchGroupControls() {
        searchFilenameCheckBox.setSelected(true);
        searchFlags.add(SearchFlag.SEARCH_BY_FILE_NAME);
        searchFilenameCheckBox.selectedProperty().addListener((_, _, enabled) ->
                applyFlag(enabled, SearchFlag.SEARCH_BY_FILE_NAME, hasSearchText()));
        commentsExistCheckBox.selectedProperty().addListener((_, _, enabled) ->
                applyFlag(enabled, SearchFlag.COMMENTS_EXIST, true));
        lyricsExistCheckBox.selectedProperty().addListener((_, _, enabled) ->
                applyFlag(enabled, SearchFlag.LYRICS_EXIST, true));
    }

    private void applyFlag(boolean enabled, SearchFlag searchFlag) {
        applyFlag(enabled, searchFlag, false);
    }

    private void applyFlag(boolean enabled, SearchFlag searchFlag, boolean autoSearch) {
        if (enabled) {
            searchFlags.add(searchFlag);
        } else {
            searchFlags.remove(searchFlag);
        }
        if (autoSearch) {
            search();
        }
    }

    private boolean hasSearchText() {
        return !searchTextField.getText().trim().isBlank();
    }

    private void setPlayqdEventHandlers() {
        searchResultTabPaneController.onPageChanged((searchFlag, page) -> {
            var supportedSearchIns = Set.of(
                    SearchFlag.SEARCH_IN_ARTISTS,
                    SearchFlag.SEARCH_IN_ALBUMS,
                    SearchFlag.SEARCH_IN_GENRES,
                    SearchFlag.SEARCH_IN_TRACKS);
            if (!supportedSearchIns.contains(searchFlag)) {
                throw new IllegalArgumentException(
                        String.format("Pageable search in %s is not supported. Supported search ins are: %s",
                                searchFlag,
                                supportedSearchIns.stream().map(Enum::name).collect(Collectors.joining(","))));
            }
            var requestParams = buildSearchRequestParams();
            var newSearchFlags = new HashSet<>(requestParams.getSearchFlags());
            newSearchFlags.removeAll(supportedSearchIns);
            newSearchFlags.add(searchFlag);
            requestParams.setSearchFlags(Collections.unmodifiableSet(newSearchFlags));
            search(requestParams, page);
        });
    }

    private void search() {
        search(buildSearchRequestParams(), 0);
    }

    private void search(SearchRequestParams requestParams, int page) {
        searchResultTabPaneController.setItems(performSearch(requestParams, page, getPageSize()));
    }

    private SearchRequestParams buildSearchRequestParams() {
        var searchRequest = new SearchRequestParams();
        searchRequest.setInput(searchTextField.getText().isEmpty() ? "" : searchTextField.getText());
        searchRequest.setSearchFlags(Collections.unmodifiableSet(searchFlags));
        return searchRequest;
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

}
