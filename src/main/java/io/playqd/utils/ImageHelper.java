package io.playqd.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public final class ImageHelper {

    public static void loadImageWithFallback(ImageView imageView,
                                             double requestedWidth,
                                             double requestedHeight,
                                             String url,
                                             String fallbackUrl) {
        var image = (Image) null;
        try {
            image = new Image(url, requestedWidth, requestedHeight, true, true, true);
        } catch (IllegalArgumentException e) {
            imageView.setImage(new Image(fallbackUrl, requestedWidth, requestedHeight, true, true, true));
            return;
        }
        image.errorProperty().addListener((_, _, hasError) -> {
            if (hasError) {
                imageView.setImage(new Image(fallbackUrl, requestedWidth, requestedHeight, true, true, true));
            }
        });
        imageView.setImage(image);
    }
}
