package io.playqd.mini.controller.configurer;

import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.item.*;

import java.util.HashMap;
import java.util.Map;

public final class ItemsViewConfigurerFactory {

    private static final Map<Class<? extends LibraryItemRow>, ItemsViewConfigurer> CONFIGURERS = new HashMap<>();

    public static ItemsViewConfigurer get(Class<? extends LibraryItemRow> type, MiniLibraryItemsViewController controller) {

        if (ArtistItemRow.class == type) {
            return CONFIGURERS.computeIfAbsent(type, _ -> new ArtistsViewConfigurer(controller));
        } else if (ArtistAlbumItemRow.class == type) {
            return CONFIGURERS.computeIfAbsent(type, _ -> new ArtistAlbumsViewConfigurer(controller));
        } else if (AlbumItemRow.class == type) {
            return CONFIGURERS.computeIfAbsent(type, _ -> new AlbumsViewConfigurer(controller));
        } else if (ArtistTrackItemRow.class == type) {
            return CONFIGURERS.computeIfAbsent(type, _ -> new ArtistTracksViewConfigurer(controller));
        } else if (AlbumTrackItemRow.class == type) {
            return CONFIGURERS.computeIfAbsent(type, _ -> new AlbumTracksViewConfigurer(controller));
        } else if (QueuedTrackItemRow.class == type) {
            return CONFIGURERS.computeIfAbsent(type, _ -> new QueuedTracksViewConfigurer(controller));
        } else if (PlaylistTrackItemRow.class == type) {
            return CONFIGURERS.computeIfAbsent(type, _ -> new PlaylistTracksViewConfigurer(controller));
        } else if (TrackItemRow.class == type) {
            return CONFIGURERS.computeIfAbsent(type, _ -> new TracksViewConfigurer(controller));
        } else if (PlaylistItemRow.class == type) {
            return CONFIGURERS.computeIfAbsent(type, _ -> new PlaylistsViewConfigurer(controller));
        } else if (CollectionItemRow.class == type) {
            return CONFIGURERS.computeIfAbsent(type, _ -> new CollectionsViewConfigurer(controller));
        } else if (CollectionChildItemRow.class == type) {
            return CONFIGURERS.computeIfAbsent(type, _ -> new CollectionItemsViewConfigurer(controller));
        } else if (WatchFolderItemRow.class == type) {
            return CONFIGURERS.computeIfAbsent(type, _ -> new WatchFoldersViewConfigurer(controller));
        } else if (FolderItemRow.class == type) {
            return CONFIGURERS.computeIfAbsent(type, _ -> new FoldersViewConfigurer(controller));
        } else {
            throw new IllegalStateException("Unsupported type " + type);
        }
    }
}
