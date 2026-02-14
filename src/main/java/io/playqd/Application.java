package io.playqd;

import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.controlsfx.dialog.ExceptionDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application extends javafx.application.Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    static {

    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
//        PlatformApi.setHostServices(getHostServices());
        startInStage(primaryStage);
    }

    private static void startInStage(Stage stage) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler(getDefaultUncaughtExceptionHandler());

//        System.setProperty("slf4j.internal.verbosity", "WARN");

        var fxmlLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.APPLICATION);

        var scene = new Scene(fxmlLoader.load(), 1600, 1000);
        scene.getStylesheets().addAll("css/glyphs.css", "css/tables.css");

        stage.setTitle("playqd-fx");
        stage.setScene(scene);
        stage.setFullScreen(false);

//        var controller = FXMLLoaderUtils.getController(fxmlLoader, ApplicationController.class);

        var logOutAndClose = (Runnable) () -> {
            LOG.info("CmdFx application is closing ...");
            stage.close();
        };

        stage.setOnCloseRequest(event -> {
            event.consume(); // to prevent automatic window closing
            if (closeConfirmed()) {
                logOutAndClose.run();
            }
        });

        stage.setOnShown(_ -> {

        });

//        scene.getStylesheets().add("css/cmdfx.css");

        stage.show();
    }

    private static boolean closeConfirmed() {
        return true;
    }

    private static Thread.UncaughtExceptionHandler getDefaultUncaughtExceptionHandler() {
        return (_, throwable) -> {
            // attempting to extract krypton exception
            var cause = throwable;
//            while (cause != null) {
//                if (cause instanceof KryptonException || cause instanceof KryptonClientException) {
//                    break;
//                } else {
//                    cause = cause.getCause();
//                }
//            }
            new ExceptionDialog(cause != null ? cause : throwable).showAndWait();
        };
    }

}
