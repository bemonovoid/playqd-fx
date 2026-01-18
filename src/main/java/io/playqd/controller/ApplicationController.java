package io.playqd.controller;

import io.playqd.event.PlayqdEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationController {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationController.class);

    @FXML
    private VBox applicationVBox;

    @FXML
    private SearchFiltersVBoxController searchFiltersVBoxController;

    @FXML
    private SearchVBoxController searchVBoxController;

    @FXML
    private void initialize() {
        setEventListeners();
    }

    private void setEventListeners() {
        setPlayqdEventListeners();
    }

    private void setPlayqdEventListeners() {
        applicationVBox.addEventHandler(PlayqdEvent.SEARCH_FLAG_CHANGED_EVENT,
                e -> searchVBoxController.onSearchFlagChangedEvent(e));
    }
}
