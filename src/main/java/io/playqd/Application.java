package io.playqd;

import io.playqd.client.PlayqdClientProvider;
import io.playqd.config.AppConfig;
import io.playqd.core.ApplicationCloseHandler;
import io.playqd.core.ApplicationExiter;
import io.playqd.core.ApplicationTitleUpdateListener;
import io.playqd.core.ApplicationUncaughtExceptionHandler;
import io.playqd.dialog.settings.ConfigName;
import io.playqd.dialog.settings.SettingsDialog;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import io.playqd.platform.PlatformApi;
import io.playqd.service.MusicLibraryScanServiceManager;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application extends javafx.application.Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static final String TITLE = "playqd-fx";

    private static ApplicationExiter applicationExiter;

    public static void main(String[] args) {
        launch(args);
    }

    public static void exit() {
        applicationExiter.doExit();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        startInStage(primaryStage);
    }

    private void startInStage(Stage stage) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler(new ApplicationUncaughtExceptionHandler());

        try {
            PlayqdClientProvider.get().health();
        } catch (Exception e) {
            new SettingsDialog(ConfigName.SERVER).afterShowAndWait(_ -> {

            });
        }

        var fxmlLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.APPLICATION);

        var scene = new Scene(fxmlLoader.load());

        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.setMaximized(true);
        scene.getStylesheets().addAll("css/glyphs.css", "css/tables.css", "css/music-library.css");

        PlatformApi.setHostServices(getHostServices());

        ApplicationTitleUpdateListener.register(stage);
        ApplicationCloseHandler.register(stage);

        stage.setOnShown(_ -> onApplicationIsShown(stage));

        stage.show();
    }

    private static void onApplicationIsShown(Stage stage) {
        if (applicationExiter == null) {
            applicationExiter = new ApplicationExiter(stage);
        }
        var rescanOnStartUp = AppConfig.getProperties().library().rescanOnStartUp().get();
        if (rescanOnStartUp) {
            MusicLibraryScanServiceManager.submitScan();
        }
    }

}
