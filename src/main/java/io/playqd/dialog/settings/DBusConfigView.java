package io.playqd.dialog.settings;

import io.playqd.dbus.MprisApplication;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DBusConfigView extends VBox implements ConfigView {

    @FXML
    private Label statusLabel, identityLabel, machineIdLabel, namesLabel;

    public DBusConfigView() {
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.DIALOG_SETTINGS_DBUS);
        resourceLoader.setRoot(this);
        resourceLoader.setController(this);
        FXMLLoaderUtils.loadObject(resourceLoader, DBusConfigView.class);
    }

    @FXML
    private void initialize() {
        var dBusInfo = MprisApplication.getInstance().getInfo();
        statusLabel.setText(dBusInfo.connected() ? "Connected" : "Disconnected");
        if (dBusInfo.connected()) {
            identityLabel.setText(dBusInfo.identity());
            machineIdLabel.setText(dBusInfo.machineId());
            var names = dBusInfo.names();
            if (names != null) {
                namesLabel.setText(String.join("\n", names));
            }
        }
    }

    @Override
    public void applyUpdates() {

    }
}
