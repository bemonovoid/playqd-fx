package io.playqd.mini.controller.factories;

import io.playqd.client.Images;
import javafx.scene.image.Image;

public final class TrackImageTableCellFactory extends RemoteImageTableCellFactory {

    @Override
    protected Image getImage(long itemId, int size) {
       return Images.track(itemId, size);
    }

    @Override
    protected Image getDefaultImage(long itemId, int size, boolean updateCache) {
        var defaultImage = Images.defaultAudioFile(size);
        if (updateCache) {
            Images.setAlbum(itemId, defaultImage, size);
        }
        return defaultImage;
    }
}
