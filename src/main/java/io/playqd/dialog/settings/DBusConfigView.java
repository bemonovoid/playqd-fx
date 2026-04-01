package io.playqd.dialog.settings;

import io.playqd.config.AppConfig;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import io.playqd.player.MprisApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;

public class DBusConfigView extends VBox implements ConfigView {

    @FXML
    private Label statusLabel, identityLabel, machineIdLabel, namesLabel;

    @FXML
    private ToggleSwitch enabledToggle;

    public DBusConfigView() {
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.DIALOG_SETTINGS_DBUS);
        resourceLoader.setRoot(this);
        resourceLoader.setController(this);
        FXMLLoaderUtils.loadObject(resourceLoader, DBusConfigView.class);
    }

    @FXML
    private void initialize() {
        enabledToggle.setSelected(MprisApplication.getInstance().isEnabled());
        enabledToggle.selectedProperty().addListener((_, _, selected) -> {
            if (selected == null) {
                return;
            }
            if (selected) {
                MprisApplication.getInstance().start();
            } else {
                MprisApplication.getInstance().close();
            }
            updateDbusInfo();

        });
        AppConfig.getProperties().player().dbus().enabled().bind(enabledToggle.selectedProperty());
        updateDbusInfo();
    }

    private void updateDbusInfo() {
        MprisApplication.getInstance().getInfo().ifPresentOrElse(dBusInfo -> {
            statusLabel.setText(dBusInfo.connected() ? "Connected" : "Disconnected");
            if (dBusInfo.connected()) {
                identityLabel.setText(dBusInfo.identity());
                machineIdLabel.setText(dBusInfo.machineId());
                var names = dBusInfo.names();
                if (names != null) {
                    namesLabel.setText(String.join("\n", names));
                }
            }
        }, () -> {
            statusLabel.setText("Disconnected");
            identityLabel.setText("n/a");
            machineIdLabel.setText("n/a");
            namesLabel.setText("n/a");
        });
    }

    @Override
    public void applyUpdates() {

    }
}
