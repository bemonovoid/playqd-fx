package io.playqd.mini.controller.item.contextmenu;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.controller.collections.CollectionDialog;
import io.playqd.data.NewMediaCollectionItem;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.TrackItemRow;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class CollectionContextMenuItemsBuilder {

    private final List<?> items;
    private final List<MenuItem> menuItems = new ArrayList<>();

    private CollectionContextMenuItemsBuilder(List<?> items) {
        this.items = items;
    }

    public static CollectionContextMenuItemsBuilder newBuilder(List<?> items) {
        return new CollectionContextMenuItemsBuilder(items);
    }

    public static List<MenuItem> buildDefault(List<?> items) {
        return newBuilder(items).addToCollectionMenu(-1).build();
    }

    public CollectionContextMenuItemsBuilder addToCollectionMenu(long excludingCollectionId) {

        var addToPlaylistMenu = new Menu("Add to collection", new FontAwesomeIconView(FontAwesomeIcon.HEADPHONES));

        var newCollection = new MenuItem("<<New collection>>");
        newCollection.setOnAction(_ -> {
            var dialog = new CollectionDialog();
            dialog.showAndWait().ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    var collectionItems = convert(items);
                    MusicLibrary.createCollection(name, collectionItems);
                }
            });
        });
        addToPlaylistMenu.getItems().add(newCollection);
        addToPlaylistMenu.getItems().add(new SeparatorMenuItem());
        addToPlaylistMenu.getItems().addAll(addToCollectionsMenuItems(excludingCollectionId));

        menuItems.add(addToPlaylistMenu);
        return this;
    }

    public CollectionContextMenuItemsBuilder moveToCollectionMenu(long excludingPlaylistId) {
        var moveToPlaylistMenu = new Menu("Move to collection", new FontAwesomeIconView(FontAwesomeIcon.CLONE));
        moveToPlaylistMenu.getItems().addAll(moveToCollectionsMenuItems(excludingPlaylistId));
        menuItems.add(moveToPlaylistMenu);
        return this;
    }

    public CollectionContextMenuItemsBuilder removeMenuItem(long fromCollection) {
        var removeMenuItem = new MenuItem("Remove from collection");
        removeMenuItem.setOnAction(_ ->
                MusicLibrary.deleteCollectionItems(fromCollection, List.of()));
        return this;
    }

    public List<MenuItem> build() {
        return Collections.unmodifiableList(menuItems);
    }

    private List<MenuItem> addToCollectionsMenuItems(long excludingCollectionId) {
        return MusicLibrary.getCollections().stream()
                .filter(p -> p.id() != excludingCollectionId)
                .map(p -> {
                    var menuItem = new MenuItem(p.name());
                    menuItem.setOnAction(_ ->
                            MusicLibrary.addItemsToCollection(p.id(), convert(items)));
                    return menuItem;
                })
                .toList();
    }

    private List<MenuItem> moveToCollectionsMenuItems(long fromCollection) {
//        return MusicLibrary.getPlaylists().stream()
//                .filter(c -> c.id() != fromCollection)
//                .map(p -> {
//                    var menuItem = new MenuItem(p.name());
//                    menuItem.setOnAction(_ ->
//                            MusicLibrary.movePlaylistTracks(fromCollection, p.id(), tracks.stream().map(Track::id).toList()));
//                    return menuItem;
//                })
//                .toList();
        return List.of();
    }

    private List<NewMediaCollectionItem> convert(List<?> sourceItems) {
        return sourceItems.stream()
                .map(i -> {
                    if (i instanceof TrackItemRow item) {
                        return NewMediaCollectionItem.create(item.getSource());
                    } else if (i instanceof LibraryItemRow item) {

                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
