package io.playqd.dialog.settings;

import io.playqd.dialog.DialogOptions;
import io.playqd.dialog.PlayqdDialog;
import io.playqd.dialog.PlayqdDialogPane;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import javafx.scene.control.DialogPane;

public class SettingsDialog extends PlayqdDialog<String> {

    public SettingsDialog() {
        this(ConfigName.GENERAL);
    }

    public SettingsDialog(ConfigName configName) {
        super(DialogOptions.builder().hideOnCloseRequest(true).build());
        setTitle("Settings");
        var dialogPane = new SettingsDialog.SettingsDialogPane(configName);
        dialogPane.setDialog(this);
        setDialogPane(dialogPane);
    }

    static class SettingsDialogPane extends PlayqdDialogPane<SettingsDialog, SettingsDialogController> {

        private final ConfigName autoSelectConfigViewName;

        public SettingsDialogPane(ConfigName configName) {
            this.autoSelectConfigViewName = configName;
            var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.DIALOG_SETTINGS);
            resourceLoader.setRoot(this);
            FXMLLoaderUtils.loadObject(resourceLoader, DialogPane.class);
            setController(FXMLLoaderUtils.getController(resourceLoader, SettingsDialogController.class));
        }

        ConfigName autoSelectConfigViewName() {
            return autoSelectConfigViewName;
        }
    }
}
