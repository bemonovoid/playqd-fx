package io.playqd.utils;

import io.playqd.controller.gallery.ArtworkGalleryView;
import io.playqd.data.Track;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ImagePopup {

    private static final Logger LOG = LoggerFactory.getLogger(ImagePopup.class);

    public static void show(Track track) {
        var title = track.artistName() + " - " + track.name();
        var imageGalleryView = new ArtworkGalleryView(track);

        var screenBounds = Screen.getPrimary().getVisualBounds();
        var maxWidth = screenBounds.getWidth() * 0.7; // 70% of screen width
        var maxHeight = screenBounds.getHeight() * 0.7; // 70% of screen height

        var scene = new Scene(imageGalleryView, maxWidth, maxHeight);

        var popupStage = new Stage();

        popupStage.setScene(scene);
        popupStage.setResizable(true);
        popupStage.setAlwaysOnTop(true);
        popupStage.setTitle(title);
        popupStage.show();
    }

}
