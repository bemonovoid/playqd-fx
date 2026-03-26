package io.playqd.core;

import javafx.stage.Stage;

public class ApplicationStartUpListeners {

    public static void register(Stage primaryStage) {
        ApplicationTitleUpdateListener.register(primaryStage);
        ApplicationCloseHandler.register(primaryStage);
    }
}
