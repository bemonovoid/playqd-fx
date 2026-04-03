package io.playqd.controller;

import io.playqd.controller.view.ObservableProperties;
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
    private SplitPane rootSplitPane, musicSplitPane, playlistsSplitPane, collectionsSplitPane,
            tracksExplorerSplitPane, foldersSplitPane;

    @FXML
    private ToggleGroup viewToggleGroup;

    @FXML
    private ToggleButton libraryViewButton, tracksViewButton, playlistsViewBtn, collectionsViewBtn, foldersViewButton;

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
            } else if (playlistsViewBtn == newToggle) {
                nodeToShow = playlistsSplitPane;
            } else if (collectionsViewBtn == newToggle) {
                nodeToShow = collectionsSplitPane;
            } else if (foldersViewButton == newToggle) {
                nodeToShow = foldersSplitPane;
            }
            if (nodeToShow != null) {
                if (rootSplitPane.getItems().size() == 1) {
                    rootSplitPane.getItems().addFirst(nodeToShow);
                } else if (rootSplitPane.getItems().getFirst() != nodeToShow) {
                    rootSplitPane.getItems().set(0, nodeToShow);
                }
            }
            rootSplitPane.setDividerPositions(0.85);
        });

        ObservableProperties.getAppViewRequestProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                switch (newValue.view()) {
                    case MUSIC_LIBRARY -> viewToggleGroup.selectToggle(libraryViewButton);
                    case COLLECTIONS -> viewToggleGroup.selectToggle(collectionsViewBtn);
                    case PLAYLISTS -> viewToggleGroup.selectToggle(playlistsViewBtn);
                    case TRACKS_EXPLORER -> viewToggleGroup.selectToggle(tracksViewButton);
                    case FOLDERS -> viewToggleGroup.selectToggle(foldersViewButton);
                }
            }
        });

        viewToggleGroup.selectToggle(libraryViewButton);
    }

}
