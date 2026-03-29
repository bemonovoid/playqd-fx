package io.playqd.controller.tracks;

import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class TracksTableFooter extends HBox {

    @FXML
    private Label selectedLabel, infoLabel;

    public TracksTableFooter() {
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.TRACKS_TABLE_FOOTER);
        resourceLoader.setRoot(this);
        resourceLoader.setController(this);
        FXMLLoaderUtils.loadObject(resourceLoader, TracksTableFooter.class);
    }

    public Label selectedLabel() {
        return selectedLabel;
    }

    public Label infoLabel() {
        return infoLabel;
    }
}
