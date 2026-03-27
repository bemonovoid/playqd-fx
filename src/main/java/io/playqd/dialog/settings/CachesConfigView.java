package io.playqd.dialog.settings;

import io.playqd.client.PlayqdClientProvider;
import io.playqd.config.AppConfig;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import io.playqd.service.MusicLibrary;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ToggleSwitch;

import java.util.concurrent.TimeUnit;

public class CachesConfigView extends VBox implements ConfigView {

    @FXML
    private ToggleSwitch clientCachesToggle, allCachesToggle;

    public CachesConfigView() {
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.DIALOG_SETTINGS_CACHES);
        resourceLoader.setRoot(this);
        resourceLoader.setController(this);
        FXMLLoaderUtils.loadObject(resourceLoader, CachesConfigView.class);
    }

    @FXML
    private void initialize() {
        clientCachesToggle.setSelected(AppConfig.getProperties().library().caches().invalidateClient().get());
        allCachesToggle.selectedProperty().addListener((_, _, selected) -> {
            var allSelected = selected != null && selected;
            if (allSelected && !clientCachesToggle.isSelected()) {
                clientCachesToggle.setSelected(true);
            }
            clientCachesToggle.setDisable(allSelected);
        });
        AppConfig.getProperties().library().caches().invalidateClient().bind(clientCachesToggle.selectedProperty());
        AppConfig.getProperties().library().caches().invalidateAll().bind(allCachesToggle.selectedProperty());
        allCachesToggle.setSelected(AppConfig.getProperties().library().caches().invalidateAll().get());
    }

    @FXML
    private void invalidateCaches() {
        if (allCachesToggle.isSelected()) {
            PlayqdClientProvider.get().evictCaches();
            Platform.runLater(() -> {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    MusicLibrary.refresh();
                } catch (Exception e) {
                    // nothing
                }
            });
        } else if (clientCachesToggle.isSelected()) {
            MusicLibrary.refresh();
        }
    }


}
