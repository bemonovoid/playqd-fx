package io.playqd.dialog.settings;

import io.playqd.config.AppConfig;
import io.playqd.dialog.PlayqdAbstractDialogController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class SettingsDialogController extends
        PlayqdAbstractDialogController<SettingsDialog.SettingsDialogPane> {

    @FXML
    private TreeView<String> settingsTreeView;

    @FXML
    private TreeItem<String> generalConfigItem, serverConfigItem, libraryConfigItem, dBusConfigItem, cachesConfigItem;

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
            } else if (cachesConfigItem == selectedItem) {
                configurationContainer.getChildren().add(new CachesConfigView());
            }
        });

        var saveBtn = ((Button) dialogPane.lookupButton(saveBtnType));
        saveBtn.setOnAction(_ -> {
            if (!configurationContainer.getChildren().isEmpty()) {
                var configView = (ConfigView) configurationContainer.getChildren().getFirst();
                configView.applyUpdates();
                AppConfig.saveProperties();
            }
        });
        var showingConfigViewName = dialogPane.autoSelectConfigViewName();
        if (showingConfigViewName != null) {
            settingsTreeView.getRoot().getChildren().stream()
                    .filter(item -> showingConfigViewName.displayName().equals(item.getValue()))
                    .findFirst()
                    .ifPresent(treeItem -> settingsTreeView.getSelectionModel().select(treeItem));
        }
    }

}
