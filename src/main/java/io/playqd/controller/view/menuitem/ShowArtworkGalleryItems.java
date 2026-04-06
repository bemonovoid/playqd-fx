package io.playqd.controller.view.menuitem;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.data.Track;
import io.playqd.utils.ImagePopup;
import javafx.scene.control.MenuItem;

import java.util.List;
import java.util.function.Supplier;

public class ShowArtworkGalleryItems implements MenuItemsBuilder {

    private final Supplier<Track> onAction;

    public ShowArtworkGalleryItems(Supplier<Track> onAction) {
        this.onAction = onAction;
    }

    @Override
    public List<MenuItem> build() {
        var menuItem = new MenuItem("Show artwork gallery", new FontAwesomeIconView(FontAwesomeIcon.IMAGE));
        menuItem.setOnAction(_ -> {
            if (onAction != null && onAction.get() != null) {
                ImagePopup.show(onAction.get());
            }
        });
        return List.of(menuItem);
    }
}
