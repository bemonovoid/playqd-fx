package io.playqd.utils;

import io.playqd.controller.gallery.ArtworkGalleryView;
import io.playqd.data.Track;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ImagePopup {

    private static final Logger LOG = LoggerFactory.getLogger(ImagePopup.class);

    public static void show(Track track) {
        var title = track.artistName() + " - " + track.title();
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

    public static void show2(Image image, String title) {
        if (image == null) {
            LOG.error("Can't show the popup, image was null.");
            return;
        }

        var screenBounds = Screen.getPrimary().getVisualBounds();
        var maxWidth = screenBounds.getWidth() * 0.7; // 70% of screen width
        var maxHeight = screenBounds.getHeight() * 0.7; // 70% of screen height

        var imageView = new ImageView(image);

        imageView.setPreserveRatio(true);
        imageView.setFitWidth(maxWidth);
        imageView.setFitHeight(maxHeight);

        var vBox = new VBox(imageView);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10, 10, 10, 10));

        var scene = new Scene(new StackPane(vBox));

        var imageTitle = title;
        if (title == null) {
            imageTitle = "no title";
        }

        var popupStage = new Stage();

        popupStage.setScene(scene);
        popupStage.setResizable(true);
        popupStage.setAlwaysOnTop(true);
        popupStage.setTitle(imageTitle);
        popupStage.show();
    }
}
