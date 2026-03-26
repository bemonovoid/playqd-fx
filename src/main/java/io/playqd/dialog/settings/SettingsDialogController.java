package io.playqd.dialog.settings;

import io.playqd.dialog.PlayqdAbstractDialogController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class SettingsDialogController extends
        PlayqdAbstractDialogController<SettingsDialog.SettingsDialogPane> {

    @FXML
    private TreeView<String> settingsTreeView;

    @FXML
    private TreeItem<String> serverConfigItem, libraryConfigItem, generalConfigItem, dBusConfigItem;

    @FXML
    private HBox configurationContainer;

    @FXML
    private ButtonType saveBtnType;

    @FXML
    private void initialize() {
        settingsTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        settingsTreeView.getSelectionModel().selectedItemProperty().addListener((_, _, selectedItem) -> {
            configurationContainer.getChildren().clear();
            if (serverConfigItem == selectedItem) {
                configurationContainer.getChildren().add(new ServerConfigView());
            } else if (libraryConfigItem == selectedItem) {
                configurationContainer.getChildren().add(new LibraryConfigView());
            } else if (dBusConfigItem == selectedItem) {
                configurationContainer.getChildren().add(new DBusConfigView());
            }
        });

        var saveBtn = ((Button) dialogPane.lookupButton(saveBtnType));
        saveBtn.setOnAction(_ -> {
            if (!configurationContainer.getChildren().isEmpty()) {
                var configView = (ConfigView) configurationContainer.getChildren().getFirst();
                configView.commitOnSave();
            }
        });
    }

}
