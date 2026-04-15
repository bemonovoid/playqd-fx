package io.playqd.mini.controller.navigator;

import io.playqd.mini.controller.item.AlbumItemRow;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.TrackItemRow;

public interface ItemsDescriptor {

    String path();

    LibraryItemRow parent();

    default boolean isEmpty() {
        return path() == null || path().isEmpty();
    }

    default boolean hasParent() {
        return parent() != null;
    }

    default boolean isPresent() {
        return !isEmpty();
    }

    static ItemsDescriptor forArtists() {
        return new ItemDescriptorImpl("artists", null);
    }

    static ItemsDescriptor forPlaylists() {
        return new ItemDescriptorImpl("playlists", null);
    }

    static ItemsDescriptor forTracks() {
        return new ItemDescriptorImpl("tracks", null);
    }

    static ItemsDescriptor forPlaylistTracks(LibraryItemRow parent) {
        return new ItemDescriptorImpl(String.format("playlists/%s", parent.getName()), parent);
    }

    static ItemsDescriptor forArtistAlbums(LibraryItemRow parent) {
        var artistName = parent.getName();
        if (parent instanceof AlbumItemRow item) {
            artistName = item.getSource().artistName();
        } else if (parent instanceof TrackItemRow item) {
            artistName = item.getSource().artistName();
        }
        return new ItemDescriptorImpl(String.format("artists/%s/albums", artistName), parent);
    }

    static ItemsDescriptor forArtistTracks(LibraryItemRow parent) {
        var artistName = parent.getName();
        if (parent instanceof AlbumItemRow item) {
            artistName = item.getSource().artistName();
        } else if (parent instanceof TrackItemRow item) {
            artistName = item.getSource().artistName();
        }
        return new ItemDescriptorImpl(String.format("artists/%s/tracks", artistName), parent);
    }

    static ItemsDescriptor forAlbumTracks(AlbumItemRow parent) {
        var path = String.format("artists/%s/albums/%s/tracks", parent.getSource().artistName(), parent.getName());
        return new ItemDescriptorImpl(path, parent);
    }

    static ItemsDescriptor forSearchArtists(String searchInput) {
        if (searchInput.isEmpty()) {
            return new ItemDescriptorImpl("[search]/artists/*", null);
        }
        return new ItemDescriptorImpl(String.format("[search]/artists/name/?contains=%s", searchInput), null);
    }

    static ItemsDescriptor forSearchAlbums(String searchInput) {
        if (searchInput.isEmpty()) {
            return new ItemDescriptorImpl("[search]/albums/*", null);
        }
        return new ItemDescriptorImpl(String.format("[search]/albums/name/?contains=%s", searchInput), null);
    }

    static ItemsDescriptor forSearchTracks(String searchInput) {
        if (searchInput.isEmpty()) {
            return new ItemDescriptorImpl("[search]/tracks/*", null);
        }
        return new ItemDescriptorImpl(String.format("[search]/tracks/name/?contains=%s", searchInput), null);
    }

    static ItemsDescriptor forSearchPlaylists(String searchInput) {
        if (searchInput.isEmpty()) {
            return new ItemDescriptorImpl("[search]/playlists/*", null);
        }
        return new ItemDescriptorImpl(String.format("[search]/playlists/name/?contains=%s", searchInput), null);
    }

    static ItemsDescriptor empty() {
        return new ItemDescriptorImpl("", null);
    }
}
