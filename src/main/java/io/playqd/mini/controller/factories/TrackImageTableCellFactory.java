package io.playqd.mini.controller.factories;

import io.playqd.client.Images;
import io.playqd.data.Track;
import io.playqd.mini.controller.item.LibraryItemRow;
import javafx.scene.image.Image;

public final class TrackImageTableCellFactory extends RemoteImageTableCellFactory {

    @Override
    protected Image getImage(LibraryItemRow row, int size) {
        if (row.getSource() instanceof Track track) {
            return Images.getImage(track, size);
        }
        return null;
    }
}
