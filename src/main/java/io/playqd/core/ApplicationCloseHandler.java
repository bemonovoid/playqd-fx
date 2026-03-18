package io.playqd.core;

import io.playqd.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationCloseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationCloseHandler.class);

    public static void register(Stage primaryStage) {
        var logOutAndClose = (Runnable) () -> {
            LOG.info("{} application is closing ...", Application.TITLE);
            primaryStage.close();
        };

        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // to prevent automatic window closing
            if (closeConfirmed()) {
                logOutAndClose.run();
            }
        });
    }

    private static boolean closeConfirmed() {
        return true;
    }
}
