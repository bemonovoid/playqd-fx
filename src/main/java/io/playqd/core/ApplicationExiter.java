package io.playqd.core;

import javafx.application.Platform;
import javafx.stage.Stage;

public class ApplicationExiter {

    private final Stage stage;

    public ApplicationExiter(Stage stage) {
        this.stage = stage;
    }

    public void doExit() {
//        Player.close();
        Platform.exit();
    }
}
