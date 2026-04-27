package io.playqd.mini.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.playqd.data.ItemType;
import io.playqd.data.LibraryItem;
import io.playqd.data.MediaCollectionItem;
import io.playqd.data.PlaylistTrack;
import io.playqd.data.Reaction;
import io.playqd.data.Track;
import io.playqd.mini.controller.item.AlbumItemRow;
import io.playqd.mini.controller.item.AlbumTrackItemRow;
import io.playqd.mini.controller.item.ArtistAlbumItemRow;
import io.playqd.mini.controller.item.ArtistItemRow;
import io.playqd.mini.controller.item.ArtistTrackItemRow;
import io.playqd.mini.controller.item.CollectionChildItemRow;
import io.playqd.mini.controller.item.CollectionItemRow;
import io.playqd.mini.controller.item.FolderItemRow;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.PlaylistItemRow;
import io.playqd.mini.controller.item.PlaylistTrackItemRow;
import io.playqd.mini.controller.item.QueuedTrackItemRow;
import io.playqd.mini.controller.item.TrackItemRow;
import io.playqd.mini.controller.item.UuidLibraryItemRow;
import io.playqd.mini.controller.item.WatchFolderItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import io.playqd.mini.controller.navigator.NavigableItems;
import io.playqd.player.Player;
import io.playqd.service.MusicLibrary;

public final class NavigableItemsResolver {

    private NavigableItemsResolver() {

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
                MusicLibrary.getCollection(item.getId()).items().stream()
                        .sorted(Comparator.comparing(MediaCollectionItem::createdDate))
                        .map(CollectionChildItemRow::new)
                        .toList());
        return new NavigableItems(ItemsDescriptor.forCollectionItems(item), supplier, CollectionChildItemRow.class);
    }

    public static NavigableItems resolvePlaylist(long id) {
        return resolvePlaylistTracks(new PlaylistItemRow(MusicLibrary.getPlaylist(id)));
    }

    public static NavigableItems resolvePlaylistTracks(PlaylistItemRow playlist) {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                playlist.getSource().tracks().stream()
                        .sorted(Comparator.comparing(PlaylistTrack::addedDate).reversed())
                        .map(plTrack -> MusicLibrary.getTrackById(plTrack.id()))
                        .map(PlaylistTrackItemRow::new)
                        .toList());
        return new NavigableItems(ItemsDescriptor.forPlaylistTracks(playlist), supplier, PlaylistTrackItemRow.class);
    }

    public static NavigableItems resolveQueuedTracks() {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                Player.queueList().stream().map(QueuedTrackItemRow::new).toList());
        return new NavigableItems(ItemsDescriptor.forQueuedTracks(), supplier, QueuedTrackItemRow.class);
    }

    public static NavigableItems resolveAlbumTracks(LibraryItemRow item) {
        var descriptor = ItemsDescriptor.forAlbumTracks(item);
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                MusicLibrary.getAlbumTracks(item.getId()).stream().map(AlbumTrackItemRow::new).toList());
        return new NavigableItems(descriptor, supplier, AlbumTrackItemRow.class);
    }

    public static NavigableItems allTracks() {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                MusicLibrary.getAllTracks().stream().map(TrackItemRow::new).toList());
        return new NavigableItems(ItemsDescriptor.forTracks(), supplier, TrackItemRow.class);
    }

    public static NavigableItems resolveLikedTracks() {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                MusicLibrary.getReactedTracks(Reaction.THUMB_UP).stream().map(TrackItemRow::new).toList());
        return new NavigableItems(ItemsDescriptor.forTracks(Map.of("liked", true)), supplier, TrackItemRow.class);
    }

    public static NavigableItems resolvePlayedTracks() {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                MusicLibrary.getPlayedTracks().stream().map(TrackItemRow::new).toList());
        return new NavigableItems(ItemsDescriptor.forTracks(Map.of("played", true)), supplier, TrackItemRow.class);
    }

    public static NavigableItems cueTracks() {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                MusicLibrary.getAllTracks().stream()
                        .filter(Track::isCueTrack)
                        .map(TrackItemRow::new)
                        .toList());
        return new NavigableItems(ItemsDescriptor.forTracks(Map.of("isCue", true)), supplier, TrackItemRow.class);
    }

    public static NavigableItems cueParentTracks() {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                MusicLibrary.getAllTracks().stream()
                        .filter(Track::isCueParentTrack)
                        .map(TrackItemRow::new)
                        .toList());
        return new NavigableItems(ItemsDescriptor.forTracks(Map.of("isCueParent", true)), supplier, TrackItemRow.class);
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
            throw new IllegalArgumentException("Parent is not of type UuidLibraryItemRow");
        }
    }

    public static NavigableItems resolveSearchArtists(SearchToken token) {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(MusicLibrary.getArtists().stream()
                .filter(filter(token))
                .map(ArtistItemRow::new)
                .sorted(Comparator.comparing(LibraryItemRow::getName))
                .toList());
        return new NavigableItems(ItemsDescriptor.forSearchArtists(token), supplier, ArtistItemRow.class);
    }

    public static NavigableItems resolveSearchAlbums(SearchToken token) {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(MusicLibrary.getAllAlbums().stream()
                .filter(filter(token))
                .map(AlbumItemRow::new)
                .sorted(Comparator.comparing(LibraryItemRow::getName))
                .toList());
        return new NavigableItems(ItemsDescriptor.forSearchAlbums(token), supplier, AlbumItemRow.class);
    }

    public static NavigableItems resolveSearchCollections(SearchToken token) {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(MusicLibrary.getCollections().stream()
                .filter(filter(token))
                .map(CollectionItemRow::new)
                .sorted(Comparator.comparing(LibraryItemRow::getName))
                .toList());
        return new NavigableItems(ItemsDescriptor.forSearchCollections(token), supplier, CollectionItemRow.class);
    }

    public static NavigableItems resolveSearchPlaylists(SearchToken token) {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(MusicLibrary.getPlaylists().stream()
                .filter(filter(token))
                .map(PlaylistItemRow::new)
                .sorted(Comparator.comparing(LibraryItemRow::getName))
                .toList());
        return new NavigableItems(ItemsDescriptor.forSearchPlaylists(token), supplier, PlaylistItemRow.class);
    }

    public static NavigableItems resolveSearchTracks(SearchToken token) {
        Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(MusicLibrary.getAllTracks().stream()
                .filter(filter(token))
                .map(TrackItemRow::new)
                .sorted(Comparator.comparing(LibraryItemRow::getName))
                .toList());
        return new NavigableItems(ItemsDescriptor.forSearchTracks(token), supplier, TrackItemRow.class);
    }

    private static Predicate<LibraryItem> filter(SearchToken token) {
        return switch (token.filterType()) {
            case CONTAINS -> libraryItem -> libraryItem.name().toLowerCase().contains(token.value());
            case STARTS_WITH -> libraryItem -> libraryItem.name().toLowerCase().startsWith(token.value());
        };
    }
}
