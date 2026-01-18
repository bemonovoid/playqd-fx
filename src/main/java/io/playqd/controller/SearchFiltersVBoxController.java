package io.playqd.controller;

import io.playqd.data.SearchFlag;
import io.playqd.event.PlayqdEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.ToggleSwitch;

public class SearchFiltersVBoxController {

    @FXML
    private VBox searchFiltersVBox;

    @FXML
    CheckComboBox<String> searchInComboBox;

    @FXML
    ToggleSwitch searchFilenameSwitch, commentsExistSwitch, lyricsExistSwitch;

    @FXML
    private void initialize() {
        initControls();
    }

    private void initControls() {
        commentsExistSwitch.selectedProperty().addListener((_, _, enabled) ->
                searchFiltersVBox.fireEvent(new PlayqdEvent.SearchFlagChangedEvent(SearchFlag.COMMENTS_EXIST, enabled)));
        lyricsExistSwitch.selectedProperty().addListener((_, _, enabled) ->
                searchFiltersVBox.fireEvent(new PlayqdEvent.SearchFlagChangedEvent(SearchFlag.LYRICS_EXIST, enabled)));
    }
}
