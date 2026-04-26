package io.playqd;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import io.playqd.mini.controller.Accelerators;
import io.playqd.platform.PlatformApi;
import io.playqd.service.MusicLibraryScanServiceManager;

public class Application extends javafx.application.Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static final String TITLE = "playqd-fx";
    private static final double PREF_HEIGHT = 750.0;
    private static final double PREF_WIDTH = 550.0;

    private static ApplicationExiter applicationExiter;

    public static void main(String[] args) {
        launch(args);
    }

    public static void exit() {
        applicationExiter.doExit();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
//        startInStage(primaryStage);
        startMiniInStage(primaryStage);
    }

    private void startMiniInStage(Stage stage) throws Exception {
        var fxmlLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.APPLICATION_MINI);
        var size = AppConfig.getProperties().ui().app().size();
        var scene = new Scene(
                fxmlLoader.load(),
                size.height().get() > 0 ? size.width().get() : PREF_WIDTH,
                size.height().get() > 0 ? size.height().get() : PREF_HEIGHT);
        stage.setTitle(TITLE);
        scene.getStylesheets().addAll("css/buttons.css", "css/mini-player.css");
        stage.setScene(scene);
        PlatformApi.setHostServices(getHostServices());
        Accelerators.initialize(scene);
        ApplicationCloseHandler.register(stage);
        ApplicationTitleUpdateListener.register(stage);
        stage.getIcons().setAll(
                new Image("/ico/app-icon-16.png"),
                new Image("/ico/app-icon-32.png"),
                new Image("/ico/app-icon-128.png"));
        stage.show();
    }

    private void startInStage(Stage stage) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler(new ApplicationUncaughtExceptionHandler());

        try {
            PlayqdClientProvider.get().health();
        } catch (Exception e) {
            new SettingsDialog(ConfigName.SERVER).afterShowAndWait(_ -> {

            });
        }

        var fxmlLoader = FXMLLoaderUtils.resourceLoader(null);

        var scene = new Scene(fxmlLoader.load());

        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.setMaximized(true);
//        scene.getStylesheets().addAll(
//                "css/glyphs.css", "css/tables.css", "css/music-library.css", "css/player.css", "css/buttons.css");

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
