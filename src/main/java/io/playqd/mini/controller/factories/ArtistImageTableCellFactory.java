package io.playqd.mini.controller.factories;

import io.playqd.client.Images;
import javafx.scene.image.Image;

public final class ArtistImageTableCellFactory extends RemoteImageTableCellFactory {

    @Override
    protected Image getImage(long itemId, int size) {
        return Images.artist(itemId, size);
    }

    @Override
    protected Image getDefaultImage(long itemId, int size, boolean updateCache) {
        var defaultImage = Images.defaultArtist(size);
        if (updateCache) {
            Images.setArtist(itemId, defaultImage, size);
        }
        return defaultImage;
    }
}
