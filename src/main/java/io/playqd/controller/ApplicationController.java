package io.playqd.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationController {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationController.class);

    @FXML
    private SplitPane rootSplitPane, musicSplitPane, tracksExplorerSplitPane;

    @FXML
    private ToggleGroup viewToggleGroup;

    @FXML
    private ToggleButton libraryViewButton, tracksViewButton;

    @FXML
    private void initialize() {
        viewToggleGroup.selectedToggleProperty().addListener((_, _, newToggle) -> {
            if (newToggle == null) {
                return;
            }
            var nodeToShow = (Node) null;
            if (tracksViewButton == newToggle) {
                nodeToShow = tracksExplorerSplitPane;
            } else if (libraryViewButton == newToggle) {
                nodeToShow = musicSplitPane;
            }
            if (nodeToShow != null) {
                if (rootSplitPane.getItems().size() == 1) {
                    rootSplitPane.getItems().addFirst(nodeToShow);
                } else {
                    rootSplitPane.getItems().set(0, nodeToShow);
                }
            }
            rootSplitPane.setDividerPositions(0.85);
        });
        viewToggleGroup.selectToggle(libraryViewButton);
    }

}
