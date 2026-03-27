package io.playqd.dialog.settings;

import io.playqd.client.ClientException;
import io.playqd.client.PlayqdClient;
import io.playqd.config.AppConfig;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

public class ServerConfigView extends VBox implements ConfigView {

    @FXML
    private Label testResultLabel;

    @FXML
    private TextField serverHostTextFld;

    public ServerConfigView() {
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.DIALOG_SETTINGS_SERVER);
        resourceLoader.setRoot(this);
        resourceLoader.setController(this);
        FXMLLoaderUtils.loadObject(resourceLoader, ServerConfigView.class);
    }

    @FXML
    private void initialize() {
        serverHostTextFld.setText(AppConfig.getProperties().serverHost().get());
        Platform.runLater(this::testConnection);
    }

    @FXML
    private void testConnection() {
        try {
            var testClient = PlayqdClient.builder().serverHost(serverHostTextFld.getText()).build();
            var health = testClient.health();
            testResultLabel.setStyle("-fx-text-fill: GREEN");
            testResultLabel.setText(health.status());
        } catch (ClientException e) {
            testResultLabel.setStyle("-fx-text-fill: RED");
            testResultLabel.setText(e.getMessage());
            testResultLabel.setTooltip(new Tooltip(testResultLabel.getText()));
        }
    }

    @Override
    public void applyUpdates() {
        AppConfig.getProperties().serverHost().set(serverHostTextFld.getText());
    }

}
