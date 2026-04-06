package io.playqd.controller.view.menuitem;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.data.Reaction;
import io.playqd.data.Track;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.MenuItem;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ReactionsMenuItems implements MenuItemsBuilder {

    private Supplier<List<Track>> onAction = Collections::emptyList;

    public ReactionsMenuItems setOnAction(Supplier<List<Track>> onAction) {
        this.onAction = onAction;
        return this;
    }

    @Override
    public List<MenuItem> build() {
        var tracks = onAction.get();

        var thumbsUpMenuItem = new MenuItem("Like", new FontAwesomeIconView(FontAwesomeIcon.THUMBS_ALT_UP));
        thumbsUpMenuItem.setOnAction(_ -> MusicLibrary.updateReaction(Reaction.THUMB_UP, tracks.stream()
                .filter(t -> Reaction.THUMB_UP != t.reaction())
                .map(Track::id)
                .toList()));

        var thumbsDownMenuItem = new MenuItem("Dislike", new FontAwesomeIconView(FontAwesomeIcon.THUMBS_ALT_DOWN));
        thumbsDownMenuItem.setOnAction(_ -> MusicLibrary.updateReaction(Reaction.THUMB_DOWN, tracks.stream()
                .filter(t -> Reaction.THUMB_DOWN != t.reaction())
                .map(Track::id)
                .toList()));

        if (tracks.isEmpty()) {
            thumbsUpMenuItem.setDisable(true);
            thumbsDownMenuItem.setDisable(true);
        }

        thumbsUpMenuItem.setDisable(tracks.stream().anyMatch(t -> Reaction.THUMB_UP == t.reaction()));
        thumbsDownMenuItem.setDisable(tracks.stream().anyMatch(t -> Reaction.THUMB_DOWN == t.reaction()));

        return List.of(thumbsUpMenuItem, thumbsDownMenuItem);
    }
}
