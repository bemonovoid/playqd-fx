package io.playqd.controller.view;

import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;

public class TracksTableHeader extends ToolBar {

    @FXML
    private Label tracksSearchLabel;

    @FXML
    MenuItem setDisplayedColumnsMenuItem;

    public TracksTableHeader() {
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.TRACKS_TABLE_HEADER);
        resourceLoader.setRoot(this);
        resourceLoader.setController(this);
        FXMLLoaderUtils.loadObject(resourceLoader, TracksTableHeader.class);
    }

    public Label tracksSearchLabel() {
        return tracksSearchLabel;
    }

}
