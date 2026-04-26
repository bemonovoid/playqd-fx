package io.playqd.core;

import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.playqd.Application;
import io.playqd.config.AppConfig;
import io.playqd.data.Track;
import io.playqd.player.Player;
import io.playqd.player.PlayerTrack;

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
                setAppSizeProperties(primaryStage);
                setPlayerStateProperties();
                AppConfig.saveProperties();
                logOutAndClose.run();
            }
        });
    }

    private static boolean closeConfirmed() {
        return true;
    }

    private static void setAppSizeProperties(Stage primaryStage) {
        var size = AppConfig.getProperties().ui().app().size();
        size.height().set(primaryStage.getHeight());
        size.width().set(primaryStage.getWidth());
    }

    private static void setPlayerStateProperties() {
        var tracklist = Player.queueList().stream().map(Track::id).toList();
        AppConfig.getProperties().player().state().tracklist().clear();
        AppConfig.getProperties().player().state().tracklist().addAll(tracklist);
        Player.playerTrack().map(PlayerTrack::track).ifPresentOrElse(track ->
                AppConfig.getProperties().player().state().lastPlayedTrackId().set(track.id()),
                () -> AppConfig.getProperties().player().state().lastPlayedTrackId().set(-1));
    }
}
