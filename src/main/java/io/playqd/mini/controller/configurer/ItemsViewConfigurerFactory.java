package io.playqd.mini.controller.configurer;

import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.item.*;

import java.util.HashMap;
import java.util.Map;

public final class ItemsViewConfigurerFactory {

    private static final Map<Class<? extends LibraryItemRow>, ItemsViewConfigurer> CONFIGURERS = new HashMap<>();

    public static ItemsViewConfigurer get(Class<? extends LibraryItemRow> type, MiniLibraryItemsViewController controller) {

        if (ArtistItemRow.class == type) {
            return CONFIGURERS.computeIfAbsent(type, _ -> new ArtistsItemsViewConfigurer(controller));
        } else if (ArtistAlbumItemRow.class == type) {
            return CONFIGURERS.computeIfAbsent(type, _ -> new ArtistAlbumsItemsViewConfigurer(controller));
        } else if (AlbumItemRow.class == type) {
            return CONFIGURERS.computeIfAbsent(type, _ -> new AlbumsItemsViewConfigurer(controller));
        } else if (AlbumTrackItemRow.class == type) {
            return CONFIGURERS.computeIfAbsent(type, _ -> new AlbumTracksItemsViewConfigurer(controller));
        } else if (QueuedTrackItemRow.class == type) {
            return CONFIGURERS.computeIfAbsent(type, _ -> new QueuedTracksItemsViewConfigurer(controller));
        } else if (TrackItemRow.class == type) {
            return CONFIGURERS.computeIfAbsent(type, _ -> new TracksItemsViewConfigurer(controller));
        } else {
            throw new IllegalStateException("Unsupported type " + type);
        }
    }
}
