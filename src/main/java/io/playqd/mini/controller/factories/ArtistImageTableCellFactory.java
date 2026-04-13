package io.playqd.mini.controller.factories;

import io.playqd.client.ArtworkImages;
import javafx.scene.image.Image;

public final class ArtistImageTableCellFactory extends ImageTableCellFactory {

    @Override
    protected Image getImage(long itemId, int size) {
        return ArtworkImages.artist(itemId, size);
    }

    @Override
    protected Image getDefaultImage(long itemId, int size, boolean updateCache) {
        var defaultImage = ArtworkImages.defaultArtist(size);
        if (updateCache) {
            ArtworkImages.setArtist(itemId, defaultImage, size);
        }
        return defaultImage;
    }
}
