package io.playqd.mini.controller.item.contextmenu;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import io.playqd.data.Track;
import io.playqd.player.PlayerTrackListManager;
import io.playqd.player.TrackListRequest;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ContextMenuItemsBuilder {

    private final List<MenuItem> menuItems = new ArrayList<>();

    private ContextMenuItemsBuilder() {

    }

    public static ContextMenuItemsBuilder newBuilder() {
        return new ContextMenuItemsBuilder();
    }

    public ContextMenuItemsBuilder playMenuItems(List<Track> tracks) {
        var playMenuItem = new MenuItem("Play now",
                FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.PLAY));
        var queueNextMenuItem = new MenuItem("Queue next",
                FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.ANGLE_DOWN));
        var queueLastMenuItem = new MenuItem("Queue last",
                FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.ANGLE_DOUBLE_DOWN));

        playMenuItem.setOnAction(_ -> PlayerTrackListManager.enqueueAndPlay(new TrackListRequest(tracks)));
        queueNextMenuItem.setOnAction(_ -> PlayerTrackListManager.addNext(tracks));
        queueLastMenuItem.setOnAction(_ -> PlayerTrackListManager.addLast(tracks));

        menuItems.addAll(List.of(playMenuItem, queueNextMenuItem, queueLastMenuItem));

        return this;
    }

    public ContextMenuItemsBuilder playlistMenuItems(List<Track> tracks) {
        menuItems.addAll(PlaylistContextMenuItemsBuilder.buildDefault(tracks));
        return this;
    }

    public List<MenuItem> build() {
        return Collections.unmodifiableList(menuItems);
    }
}
