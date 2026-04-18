package io.playqd.mini.controller;

import io.playqd.data.ItemType;
import io.playqd.mini.controller.item.*;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import io.playqd.mini.controller.navigator.NavigableItems;
import io.playqd.player.PlayerTrackListManager;
import io.playqd.service.MusicLibrary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class NavigableItemsResolver {

    private NavigableItemsResolver() {

    }

    public static NavigableItems resolveSearchArtists(String token) {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(MusicLibrary.getArtists().stream()
                .filter(a -> a.name().toLowerCase().contains(token))
                .map(ArtistItemRow::new)
                .toList());
        return new NavigableItems(ItemsDescriptor.forSearchArtists(token), supplier, ArtistItemRow.class);
    }

    public static NavigableItems resolveSearchAlbums(String token) {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(MusicLibrary.getAllAlbums().stream()
                .filter(a -> a.name().toLowerCase().contains(token))
                .map(AlbumItemRow::new)
                .toList());
        return new NavigableItems(ItemsDescriptor.forSearchAlbums(token), supplier, AlbumItemRow.class);
    }

    public static NavigableItems resolveSearchTracks(String token) {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(MusicLibrary.getAllTracks().stream()
                .filter(t -> t.name().toLowerCase().contains(token))
                .map(TrackItemRow::new)
                .toList());
        return new NavigableItems(ItemsDescriptor.forSearchTracks(token), supplier, TrackItemRow.class);
    }

    public static NavigableItems resolveSearchPlaylists(String token) {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(MusicLibrary.getPlaylists().stream()
                .filter(p -> p.name().toLowerCase().contains(token))
                .map(PlaylistItemRow::new)
                .toList());
        return new NavigableItems(ItemsDescriptor.forSearchPlaylists(token), supplier, PlaylistItemRow.class);
    }

    public static NavigableItems resolveSearchCollections(String token) {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(MusicLibrary.getCollections().stream()
                .filter(p -> p.name().toLowerCase().contains(token))
                .map(CollectionItemRow::new)
                .toList());
        return new NavigableItems(ItemsDescriptor.forSearchCollections(token), supplier, CollectionItemRow.class);
    }

    public static NavigableItems resolveArtistAlbums(LibraryItemRow item) {
        Supplier<List<LibraryItemRow>> albumsSupplier = () -> new ArrayList<>(
                MusicLibrary.getArtistAlbums(item.getId()).stream().map(ArtistAlbumItemRow::new).toList());
        return new NavigableItems(
                ItemsDescriptor.forArtistAlbums(item), albumsSupplier, ArtistAlbumItemRow.class);
    }

    public static NavigableItems resolveArtistTracks(LibraryItemRow item) {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                MusicLibrary.getArtistTracks(item.getId()).stream().map(ArtistTrackItemRow::new).toList());
        return new NavigableItems(
                ItemsDescriptor.forArtistTracks(item), supplier, ArtistTrackItemRow.class);
    }

    public static NavigableItems resolveArtists() {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                MusicLibrary.getArtists().stream().map(ArtistItemRow::new).toList());
        return new NavigableItems(ItemsDescriptor.forArtists(), supplier, ArtistItemRow.class);
    }

    public static NavigableItems resolveAlbums() {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                MusicLibrary.getAllAlbums().stream().map(AlbumItemRow::new).toList());
        return new NavigableItems(ItemsDescriptor.forArtists(), supplier, AlbumItemRow.class);
    }

    public static NavigableItems resolveGenres() {
        Supplier<List<LibraryItemRow>> supplier = Collections::emptyList;
        return new NavigableItems(ItemsDescriptor.empty(), supplier, AlbumItemRow.class);
    }

    public static NavigableItems resolvePlaylists() {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                MusicLibrary.getPlaylists().stream().map(PlaylistItemRow::new).toList());
        return new NavigableItems(ItemsDescriptor.forPlaylists(), supplier, PlaylistItemRow.class);
    }

    public static NavigableItems resolveCollections() {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                MusicLibrary.getCollections().stream().map(CollectionItemRow::new).toList());
        return new NavigableItems(ItemsDescriptor.forCollections(), supplier, CollectionItemRow.class);
    }

    public static NavigableItems resolveCollectionItems(LibraryItemRow item) {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                MusicLibrary.getCollection(item.getId()).items().stream().map(CollectionChildItemRow::new).toList());
        return new NavigableItems(ItemsDescriptor.forCollectionItems(item), supplier, CollectionChildItemRow.class);
    }

    public static NavigableItems resolvePlaylist(long id) {
        return resolvePlaylistTracks(new PlaylistItemRow(MusicLibrary.getPlaylist(id)));
    }

    public static NavigableItems resolvePlaylistTracks(PlaylistItemRow playlist) {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                playlist.getSource().tracks().stream()
                        .map(plTrack -> MusicLibrary.getTrackById(plTrack.id()))
                        .map(PlaylistTrackItemRow::new)
                        .toList());
        return new NavigableItems(ItemsDescriptor.forPlaylistTracks(playlist), supplier, PlaylistTrackItemRow.class);
    }

    public static NavigableItems resolveQueuedTracks() {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                PlayerTrackListManager.trackList().stream().map(QueuedTrackItemRow::new).toList());
        return new NavigableItems(ItemsDescriptor.empty(), supplier, QueuedTrackItemRow.class);
    }

    public static NavigableItems resolveAlbumTracks(LibraryItemRow item) {
        var descriptor = ItemsDescriptor.forAlbumTracks(item);
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                MusicLibrary.getAlbumTracks(item.getId()).stream().map(AlbumTrackItemRow::new).toList());
        return new NavigableItems(descriptor, supplier, AlbumTrackItemRow.class);
    }

    public static NavigableItems resolveTracks() {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                MusicLibrary.getAllTracks().stream().map(TrackItemRow::new).toList());
        return new NavigableItems(ItemsDescriptor.forTracks(), supplier, TrackItemRow.class);
    }

    public static NavigableItems resolveWatchFolders() {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                MusicLibrary.getWatchFolders().stream().map(WatchFolderItemRow::new).toList());
        return new NavigableItems(ItemsDescriptor.forWatchFolders(), supplier, WatchFolderItemRow.class);
    }

    public static NavigableItems resolveWatchFolderItems(LibraryItemRow parent) {
        if (parent instanceof UuidLibraryItemRow uuidLibraryItemRow) {
            Supplier<List<LibraryItemRow>> supplier = () -> {
                var itemsByType = MusicLibrary.getWatchFolderItems(uuidLibraryItemRow.getUUID()).stream()
                        .map(FolderItemRow::new)
                        .collect(Collectors.groupingBy(i -> i.getSource().itemType()));
                var folders = new ArrayList<>(itemsByType.getOrDefault(ItemType.FOLDER, Collections.emptyList()));
                folders.sort(Comparator.comparing(f -> f.getSource().name()));
                var otherItems = itemsByType.entrySet().stream().filter(e -> e.getKey() != ItemType.FOLDER)
                        .flatMap(e -> e.getValue().stream())
                        .sorted(Comparator.comparing(i -> i.getSource().name()))
                        .toList();
                var result = new ArrayList<LibraryItemRow>(folders.size() + otherItems.size());
                result.addAll(folders);
                result.addAll(otherItems);
                return result;
            };
            var descriptor = ItemsDescriptor.forWatchFolderItems(parent);
            return new NavigableItems(descriptor, supplier, FolderItemRow.class);
        } else {
            throw new  IllegalArgumentException("Parent is not of type UuidLibraryItemRow");
        }

    }
}
