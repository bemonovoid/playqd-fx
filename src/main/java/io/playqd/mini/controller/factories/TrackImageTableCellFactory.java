package io.playqd.mini.controller.factories;

import io.playqd.client.ArtworkImages;
import javafx.scene.image.Image;

public class TrackImageTableCellFactory extends ImageTableCellFactory {

    @Override
    protected Image getImage(long itemId, int size) {
       return ArtworkImages.album(itemId, size);
    }

    @Override
    protected Image getDefaultImage(long itemId, int size, boolean updateCache) {
        var defaultImage = ArtworkImages.defaultAlbum(size);
        if (updateCache) {
            ArtworkImages.setAlbum(itemId, defaultImage, size);
        }
        return defaultImage;
    }
}
