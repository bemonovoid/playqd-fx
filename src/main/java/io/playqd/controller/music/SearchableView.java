package io.playqd.controller.music;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

abstract class SearchableView implements Searchable {

    private static final Logger LOG = LoggerFactory.getLogger(SearchableView.class);

    private final SimpleListProperty<Character> searchTextInput =
            new SimpleListProperty<>(FXCollections.observableArrayList());

    private Runnable onCleared;

    void initialize(Node keyEventNode, Consumer<String> resultConsumer, Runnable onCleared) {
        this.onCleared = onCleared;
        searchTextInput.addListener((_, _, newInput) -> {
            if (newInput.isEmpty()) {
                resultConsumer.accept("");
            } else {
                var inputString = newInput.stream().map(Objects::toString).collect(Collectors.joining()).toLowerCase();
                resultConsumer.accept(inputString);
            }
        });
        keyEventNode.setOnKeyPressed(this::searchFromKeyEvent);
    }

    private void searchFromKeyEvent(KeyEvent keyEvent) {
        var keyCode = keyEvent.getCode();
        if (KeyCode.ESCAPE == keyCode) {
            clearSearchTextInput();
        } else if (KeyCode.BACK_SPACE == keyCode) {
            removeLastSearchInputCharacter();
        } else if (isSearchInputTextKeyEvent(keyEvent)) {
            searchTextInput.add(((char) keyCode.getCode()));
        }
    }

    private void removeLastSearchInputCharacter() {
        if (searchTextInput.size() == 1) {
            clearSearchTextInput();
        } else if (!searchTextInput.isEmpty()) {
            searchTextInput.get().removeLast();
        }
    }

    private void clearSearchTextInput() {
        searchTextInput.clear();
        LOG.info("Search input text was cleared.");
        onCleared.run();
    }

    private static boolean isSearchInputTextKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.isShortcutDown()) {
            return false;
        }
        var keyCode = keyEvent.getCode();
        return keyCode.isDigitKey() || keyCode.isLetterKey() || KeyCode.SPACE == keyCode;
    }
}
