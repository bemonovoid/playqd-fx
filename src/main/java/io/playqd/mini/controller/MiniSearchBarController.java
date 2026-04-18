package io.playqd.mini.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.controlsfx.control.textfield.CustomTextField;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;


public class MiniSearchBarController {

    private final AtomicBoolean wasInputAlreadyEmptied = new AtomicBoolean(false);

    private BiConsumer<SearchScope, String> onSearchSubmit;

    @FXML
    private HBox searchInputLeftNode;

    @FXML
    private CustomTextField searchInputTextFld;

    @FXML
    private MenuButton quickNavItemsMenuBtn;

    @FXML
    private void initialize() {
        searchInputTextFld.textProperty().addListener((_, oldValue, newValue) -> onSearchInputChanged(oldValue, newValue));
        searchInputTextFld.setOnKeyPressed(this::onSearchInputKeyPressed);
        initQuickNavItemsMenuBtn();
    }

    private void initQuickNavItemsMenuBtn() {
        quickNavItemsMenuBtn.getStyleClass().setAll("button", "icon-button");
        quickNavItemsMenuBtn.getItems().addAll(QuickNavigationMenuItems.get(quickNavItemsMenuBtn));
    }

    private void onSearchInputChanged(String oldValue, String newValue) {
        if (newValue == null) {
            return;
        }
        if (newValue.isEmpty()) {
            // TODO remove tag
            return;
        }
        if (wasInputAlreadyEmptied.get()) {
            wasInputAlreadyEmptied.set(false);
        }
    }

    private void onSearchInputKeyPressed(KeyEvent keyEvent) {
        var inputText = searchInputTextFld.getText();
        if (KeyCode.TAB == keyEvent.getCode()) {
            if (searchScopeTagIsHidden()) {
                if (inputText.equalsIgnoreCase(SearchScope.ARTISTS.shortcut())) {
                    clearSearchInputText();
                    showSearchScopeTag(SearchScope.ARTISTS);
                } else if (inputText.equalsIgnoreCase(SearchScope.ALBUMS.shortcut())) {
                    clearSearchInputText();
                    showSearchScopeTag(SearchScope.ALBUMS);
                } else if (inputText.equalsIgnoreCase(SearchScope.TRACKS.shortcut())) {
                    clearSearchInputText();
                    showSearchScopeTag(SearchScope.TRACKS);
                } else if (inputText.equalsIgnoreCase(SearchScope.PLAYLISTS.shortcut())) {
                    clearSearchInputText();
                    showSearchScopeTag(SearchScope.PLAYLISTS);
                } else if (inputText.equalsIgnoreCase(SearchScope.COLLECTIONS.shortcut())) {
                    clearSearchInputText();
                    showSearchScopeTag(SearchScope.COLLECTIONS);
                }
            }
        } else if (KeyCode.ESCAPE == keyEvent.getCode()) {
            if (inputText.isEmpty()) {
                hideSearchScopeTagIfShown();
            } else {
                clearSearchInputText();
                wasInputAlreadyEmptied.set(true);
            }
        } else if (KeyCode.DELETE == keyEvent.getCode() || KeyCode.BACK_SPACE == keyEvent.getCode()) {
            if (inputText.isEmpty()) {
                hideSearchScopeTagIfShown();
            }
        } else if (KeyCode.ENTER == keyEvent.getCode()) {
            onSearchSubmit.accept(getSearchScope(), inputText);
            searchInputLeftNode.requestFocus();
        }
    }

    private void hideSearchScopeTagIfShown() {
        if (searchScopeTagIsShown()) {
            if (wasInputAlreadyEmptied.get()) {
                wasInputAlreadyEmptied.set(false);
                hideSearchScopeTag();
            } else {
                wasInputAlreadyEmptied.set(true);
            }
        }
    }

    private void clearSearchInputText() {
        searchInputTextFld.clear();
    }

    private boolean searchScopeTagIsHidden() {
        return !searchScopeTagIsShown();
    }

    private boolean searchScopeTagIsShown() {
        return searchInputLeftNode.getChildren().size() == 2;
    }

    private void showSearchScopeTag(SearchScope searchScope) {
        searchInputLeftNode.getChildren().addLast(createSearchScopeTag(searchScope));
        searchInputTextFld.requestFocus();
    }

    private void hideSearchScopeTag() {
        searchInputLeftNode.getChildren().removeLast();
        clearSearchInputText();

    }

    private SearchScope getSearchScope() {
        if (searchScopeTagIsHidden()) {
            return null;
        }
        return (SearchScope) searchInputLeftNode.getChildren().getLast().getUserData();
    }

    private Label createSearchScopeTag(SearchScope searchScope) {
        var label = new Label(searchScope.displayText());
        label.setUserData(searchScope);
        label.setMinWidth(15);
        label.setMinHeight(15);
        label.getStyleClass().add("search-input-scope-label");
        return label;
    }

    void setOnSearchSubmit(BiConsumer<SearchScope, String> onSearchSubmit) {
        this.onSearchSubmit = onSearchSubmit;
    }

}
