package io.playqd.mini.controller.factories;

import io.playqd.client.Images;
import javafx.scene.image.Image;

public final class AlbumImageTableCellFactory extends RemoteImageTableCellFactory {

    @Override
    protected Image getImage(long itemId, int size) {
        return Images.album(itemId, size);
    }

    @Override
    protected Image getDefaultImage(long itemId, int size, boolean updateCache) {
        var defaultImage = Images.defaultAlbum(size);
        if (updateCache) {
            Images.setAlbum(itemId, defaultImage, size);
        }
        return defaultImage;
    }

}
