package io.playqd.mini.controller.factories;

import io.playqd.client.Images;
import io.playqd.data.Album;
import io.playqd.data.Artist;
import io.playqd.mini.controller.item.LibraryItemRow;
import javafx.scene.image.Image;

public final class ArtistImageTableCellFactory extends RemoteImageTableCellFactory {

    @Override
    protected Image getImage(LibraryItemRow row, int size) {
        if (row.getSource() instanceof Artist artist) {
            return Images.getImage(artist, size);
        }
        return null;
    }
}
