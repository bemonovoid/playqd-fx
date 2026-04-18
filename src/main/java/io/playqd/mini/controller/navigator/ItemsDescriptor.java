package io.playqd.mini.controller.navigator;

import io.playqd.mini.controller.item.AlbumItemRow;
import io.playqd.mini.controller.item.FolderItemRow;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.TrackItemRow;

import java.util.List;

public interface ItemsDescriptor {

    ItemPath path();

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
        return new ItemDescriptorImpl("artists");
    }

    static ItemsDescriptor forPlaylists() {
        return new ItemDescriptorImpl("playlists");
    }

    static ItemsDescriptor forCollections() {
        return new ItemDescriptorImpl("collections");
    }

    static ItemsDescriptor forCollectionItems(LibraryItemRow parent) {
        return new ItemDescriptorImpl(String.format("collections/%s", parent.getName()), parent);
    }

    static ItemsDescriptor forTracks() {
        return new ItemDescriptorImpl("tracks");
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

    static ItemsDescriptor forAlbumTracks(LibraryItemRow parent) {
        var artistName = parent.getName();
        var albumName = parent.getName();
        if (parent instanceof AlbumItemRow item) {
            artistName = item.getSource().artistName();
        } else if (parent instanceof TrackItemRow item) {
            artistName = item.getSource().artistName();
            albumName = item.getSource().albumName();
        }
        var itemPath = new ItemPath("artists/%s/albums/%s/tracks", List.of(artistName, albumName));
        return new ItemDescriptorImpl(itemPath, parent);
    }

    static ItemsDescriptor forWatchFolders() {
        return new ItemDescriptorImpl("folders");
    }

    static ItemsDescriptor forWatchFolderItems(LibraryItemRow parent) {
        var path = "";
        if (parent instanceof FolderItemRow folderItemRow) {
            path = folderItemRow.getSource().location();
        }
        return new ItemDescriptorImpl(path, parent);
    }

    static ItemsDescriptor forSearchArtists(String searchInput) {
        if (searchInput.isEmpty()) {
            return new ItemDescriptorImpl("[search]/artists/*");
        }
        return new ItemDescriptorImpl(String.format("[search]/artists/name/?contains=%s", searchInput));
    }

    static ItemsDescriptor forSearchAlbums(String searchInput) {
        if (searchInput.isEmpty()) {
            return new ItemDescriptorImpl("[search]/albums/*");
        }
        return new ItemDescriptorImpl(String.format("[search]/albums/name/?contains=%s", searchInput));
    }

    static ItemsDescriptor forSearchTracks(String searchInput) {
        if (searchInput.isEmpty()) {
            return new ItemDescriptorImpl("[search]/tracks/*");
        }
        return new ItemDescriptorImpl(String.format("[search]/tracks/name/?contains=%s", searchInput));
    }

    static ItemsDescriptor forSearchPlaylists(String searchInput) {
        if (searchInput.isEmpty()) {
            return new ItemDescriptorImpl("[search]/playlists/*");
        }
        return new ItemDescriptorImpl(String.format("[search]/playlists/name/?contains=%s", searchInput));
    }

    static ItemsDescriptor forSearchCollections(String searchInput) {
        if (searchInput.isEmpty()) {
            return new ItemDescriptorImpl("[search]/collections/*");
        }
        return new ItemDescriptorImpl(String.format("[search]/collections/name/?contains=%s", searchInput));
    }

    static ItemsDescriptor empty() {
        return new ItemDescriptorImpl("");
    }
}
