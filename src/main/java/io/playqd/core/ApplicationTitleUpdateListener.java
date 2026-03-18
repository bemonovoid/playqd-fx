package io.playqd.core;

import io.playqd.Application;
import io.playqd.player.Player;
import javafx.application.Platform;
import javafx.stage.Stage;

public class ApplicationTitleUpdateListener {

    public static void register(Stage primaryStage) {
        Player.onPlayingTrackChanged(track ->
                setTitle(primaryStage, track.artistName() + " - " + track.title()),
                () -> setTitle(primaryStage, Application.TITLE));
    }

    private static void setTitle(Stage primaryStage, String newTitle) {
        Platform.runLater(() -> primaryStage.setTitle(newTitle));
    }
}
