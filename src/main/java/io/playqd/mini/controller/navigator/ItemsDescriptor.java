package io.playqd.mini.controller.navigator;

@FunctionalInterface
public interface ItemsDescriptor {

    String get();

    default boolean isEmpty() {
        return get() == null || get().isEmpty();
    }

    default boolean isPresent() {
        return !isEmpty();
    }

    static ItemsDescriptor forArtists() {
        return () -> "artists";
    }

    static ItemsDescriptor forArtistAlbums(String artistName) {
        return () -> String.format("artists/%s/albums", artistName);
    }

    static ItemsDescriptor forArtistTracks(String artistName) {
        return () -> String.format("artists/%s/tracks", artistName);
    }

    static ItemsDescriptor forAlbumTracks(String artistName, String albumName) {
        return () -> String.format("artists/%s/albums/%s/tracks", artistName, albumName);
    }

    static ItemsDescriptor forSearchArtists(String searchInput) {
        if (searchInput.isEmpty()) {
            return () -> "[search]/artists/*";
        }
        return () -> String.format("[search]/artists/name/?contains=%s", searchInput);
    }

    static  ItemsDescriptor forSearchAlbums(String searchInput) {
        if (searchInput.isEmpty()) {
            return () -> "[search]/albums/*";
        }
        return () -> String.format("[search]/albums/name/?contains=%s", searchInput);
    }

    static  ItemsDescriptor forSearchTracks(String searchInput) {
        if (searchInput.isEmpty()) {
            return () -> "[search]/tracks/*";
        }
        return () -> String.format("[search]/tracks/name/?contains=%s", searchInput);
    }

    static ItemsDescriptor empty() {
        return () -> "";
    }
}
