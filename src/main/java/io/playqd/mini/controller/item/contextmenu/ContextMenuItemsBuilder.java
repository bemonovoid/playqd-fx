package io.playqd.mini.controller.item.contextmenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import io.playqd.data.Track;
import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.player.Player;
import io.playqd.player.TrackListRequest;

public final class ContextMenuItemsBuilder {

    private final List<MenuItem> menuItems = new ArrayList<>();
    private final MiniLibraryItemsViewController controller;

    private ContextMenuItemsBuilder(MiniLibraryItemsViewController controller) {
        this.controller = controller;
    }

    public static ContextMenuItemsBuilder newBuilder() {
        return newBuilder(null);
    }

    public static ContextMenuItemsBuilder newBuilder(MiniLibraryItemsViewController controller) {
        return new ContextMenuItemsBuilder(controller);
    }

    public ContextMenuItemsBuilder playMenuItems(List<Track> tracks) {
        var playMenuItem = new MenuItem("Play now",
                FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.PLAY));
        playMenuItem.setOnAction(_ -> Player.enqueue(new TrackListRequest(tracks)));
        menuItems.add(playMenuItem);
        return this;
    }

    public ContextMenuItemsBuilder addToQueueMenuItems(List<Track> tracks) {
        var queueNextMenuItem = new MenuItem("Queue next",
                FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.ANGLE_DOWN));
        var queueLastMenuItem = new MenuItem("Queue last",
                FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.ANGLE_DOUBLE_DOWN));
        queueNextMenuItem.setOnAction(_ -> Player.addNext(tracks));
        queueLastMenuItem.setOnAction(_ -> Player.addLast(tracks));
        menuItems.addAll(List.of(queueNextMenuItem, queueLastMenuItem));
        return this;
    }

    public ContextMenuItemsBuilder removeFromQueueMenuItems(List<Track> tracks) {
        var removeFromQueue = new MenuItem("Remove from queue",
                FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.TIMES));
        removeFromQueue.setOnAction(_ -> Player.remove(tracks));
        menuItems.add(removeFromQueue);
        return this;
    }

    public ContextMenuItemsBuilder playlistMenuItems(List<Track> tracks) {
        menuItems.addAll(PlaylistContextMenuItemsBuilder.buildDefault(tracks));
        return this;
    }

    public ContextMenuItemsBuilder collectionsMenuItems(List<LibraryItemRow> items) {
        menuItems.addAll(CollectionContextMenuItemsBuilder.buildDefault(items));
        return this;
    }

    public ContextMenuItemsBuilder showInContextMenuItems(List<LibraryItemRow> items) {
        menuItems.addAll(ShowInContextMenuItemsBuilder.buildDefault(controller, items));
        return this;
    }

    public ContextMenuItemsBuilder separatorMenuItem() {
        menuItems.add(new SeparatorMenuItem());
        return this;
    }

    public List<MenuItem> build() {
        return Collections.unmodifiableList(menuItems);
    }
}
