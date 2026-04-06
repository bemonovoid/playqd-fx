package io.playqd.controller.view.menuitem;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.controller.view.ObservableProperties;
import io.playqd.controller.view.request.FoldersViewRequest;
import javafx.scene.control.MenuItem;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

public class ShowInFolderItems implements MenuItemsBuilder {

    private final Supplier<Path> onAction;

    public ShowInFolderItems(Supplier<Path> onAction) {
        this.onAction = onAction;
    }

    @Override
    public List<MenuItem> build() {
        var menuItem = new MenuItem("Show in folder", new FontAwesomeIconView(FontAwesomeIcon.FOLDER_OPEN_ALT));
        menuItem.setOnAction(_ -> {
            if (onAction != null && onAction.get() != null) {
                ObservableProperties.setAppViewRequestProperty(new FoldersViewRequest(onAction.get()));
            }
        });
        return List.of(menuItem);
    }
}
