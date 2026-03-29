package io.playqd.controller.view.menuitem;


import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import io.playqd.data.Track;
import io.playqd.player.PlayerTrackListManager;
import io.playqd.player.TrackListRequest;
import javafx.scene.control.MenuItem;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class PlayMenuItems implements MenuItemsBuilder {

    private Supplier<List<Track>> tracksSupplier = Collections::emptyList;

    public PlayMenuItems onPlay(Supplier<List<Track>> tracksSupplier) {
        this.tracksSupplier = tracksSupplier;
        return this;
    }

    @Override
    public List<MenuItem> build() {
        var tracks = getTracksToPlay();
        var playMenuItem = new MenuItem(
                "Play now", FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.PLAY));

        playMenuItem.setOnAction(_ -> PlayerTrackListManager.enqueue(new TrackListRequest(tracks)));

        var queueNextMenuItem = new MenuItem(
                "Queue next", FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.ANGLE_DOWN));
        queueNextMenuItem.setOnAction(_ -> PlayerTrackListManager.addNext(tracks));

        var queueLastMenuItem = new MenuItem(
                "Queue last", FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.ANGLE_DOUBLE_DOWN));
        queueLastMenuItem.setOnAction(_ -> PlayerTrackListManager.addLast(tracks));

        var menuItems = List.of(playMenuItem, queueNextMenuItem, queueLastMenuItem);

        if (tracks.isEmpty()) {
            menuItems.forEach(mi -> mi.setDisable(true));
        }

        return menuItems;
    }

    private List<Track> getTracksToPlay() {
        if (tracksSupplier != null) {
            var itemsToPlay = tracksSupplier.get();
            if (itemsToPlay != null) {
                return itemsToPlay;
            }
        }
        return Collections.emptyList();
    }
}
