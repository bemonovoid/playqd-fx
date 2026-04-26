package io.playqd.mini.controller.navigator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.playqd.mini.controller.SearchToken;
import io.playqd.mini.controller.item.AlbumItemRow;
import io.playqd.mini.controller.item.FolderItemRow;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.TrackItemRow;

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
        return forTracks(null);
    }

    static ItemsDescriptor forTracks(Map<String, Object> requestParams) {
        if (requestParams != null && !requestParams.isEmpty()) {
            var params = requestParams.entrySet().stream()
                    .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue().toString()))
                    .collect(Collectors.joining("&"));
            return new ItemDescriptorImpl("tracks/" + params);
        }
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

    static ItemsDescriptor forSearchArtists(SearchToken token) {
        if (token.isEmpty()) {
            return new ItemDescriptorImpl("[search]/artists/*");
        }
        var path =  switch (token.filterType()) {
            case CONTAINS -> String.format("[search]/artists/nameContains=%s", token.value());
            case STARTS_WITH -> String.format("[search]/artists/nameStartsWith=%s", token.value());
        };
        return new ItemDescriptorImpl(path);
    }

    static ItemsDescriptor forSearchAlbums(SearchToken token) {
        if (token.isEmpty()) {
            return new ItemDescriptorImpl("[search]/albums/*");
        }
        var path =  switch (token.filterType()) {
            case CONTAINS -> String.format("[search]/albums/nameContains=%s", token.value());
            case STARTS_WITH -> String.format("[search]/albums/nameStartsWith=%s", token.value());
        };
        return new ItemDescriptorImpl(path);
    }

    static ItemsDescriptor forSearchTracks(SearchToken token) {
        if (token.isEmpty()) {
            return new ItemDescriptorImpl("[search]/tracks/*");
        }
        var path =  switch (token.filterType()) {
            case CONTAINS -> String.format("[search]/tracks/nameContains=%s", token.value());
            case STARTS_WITH -> String.format("[search]/tracks/nameStartsWith=%s", token.value());
        };
        return new ItemDescriptorImpl(path);
    }

    static ItemsDescriptor forSearchPlaylists(SearchToken token) {
        if (token.isEmpty()) {
            return new ItemDescriptorImpl("[search]/playlists/*");
        }
        var path =  switch (token.filterType()) {
            case CONTAINS -> String.format("[search]/playlists/nameContains=%s", token.value());
            case STARTS_WITH -> String.format("[search]/playlists/nameStartsWith=%s", token.value());
        };
        return new ItemDescriptorImpl(path);
    }

    static ItemsDescriptor forSearchCollections(SearchToken token) {
        if (token.isEmpty()) {
            return new ItemDescriptorImpl("[search]/collections/*");
        }
        var path =  switch (token.filterType()) {
            case CONTAINS -> String.format("[search]/collections/nameContains=%s", token.value());
            case STARTS_WITH -> String.format("[search]/collections/nameStartsWith=%s", token.value());
        };
        return new ItemDescriptorImpl(path);
    }

    static ItemsDescriptor empty() {
        return new ItemDescriptorImpl("");
    }
}
