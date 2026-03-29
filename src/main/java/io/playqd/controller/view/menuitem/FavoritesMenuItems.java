package io.playqd.controller.view.menuitem;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.data.Track;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.MenuItem;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class FavoritesMenuItems implements MenuItemsBuilder {

    private Supplier<List<Track>> onAction = Collections::emptyList;

    public FavoritesMenuItems setOnAction(Supplier<List<Track>> onAction) {
        this.onAction = onAction;
        return this;
    }

    @Override
    public List<MenuItem> build() {
        var tracks = onAction.get();

        var likeMenuItem = new MenuItem("Like", new FontAwesomeIconView(FontAwesomeIcon.THUMBS_ALT_UP));
        likeMenuItem.setOnAction(_ -> MusicLibrary.like(tracks.stream()
                .filter(t -> t.rating().value() <= 0)
                .map(Track::id)
                .toList()));

        var dislikeMenuItem = new MenuItem("Dislike", new FontAwesomeIconView(FontAwesomeIcon.THUMBS_ALT_DOWN));
        dislikeMenuItem.setOnAction(_ -> MusicLibrary.unlike(tracks.stream()
                .filter(t -> t.rating().value() > 0)
                .map(Track::id)
                .toList()));

        if (tracks.isEmpty()) {
            likeMenuItem.setDisable(true);
            dislikeMenuItem.setDisable(true);
        }

        likeMenuItem.setDisable(tracks.stream().allMatch(t -> t.rating().value() > 0));
        dislikeMenuItem.setDisable(tracks.stream().allMatch(t -> t.rating().value() <= 0));

        return List.of(likeMenuItem, dislikeMenuItem);
    }
}
