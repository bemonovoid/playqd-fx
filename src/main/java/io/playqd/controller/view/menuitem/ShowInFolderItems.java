package io.playqd.controller.view.menuitem;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.controller.view.ObservableProperties;
import io.playqd.controller.view.request.FoldersViewRequest;
import io.playqd.data.Track;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.MenuItem;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ShowInFolderItems implements MenuItemsBuilder {

    private Supplier<List<Track>> onAction = Collections::emptyList;

    public ShowInFolderItems setOnAction(Supplier<List<Track>> onAction) {
        this.onAction = onAction;
        return this;
    }

    @Override
    public List<MenuItem> build() {
        var menuItem = new MenuItem("Show in folder", new FontAwesomeIconView(FontAwesomeIcon.FOLDER_OPEN_ALT));
        menuItem.setOnAction(_ -> {
            if (onAction != null && onAction.get() != null) {
                var track = onAction.get().getFirst();
                if (track.isCueTrack()) {
                    track = MusicLibrary.getTrackById(track.parentId());
                }
                ObservableProperties.setAppViewRequestProperty(
                        new FoldersViewRequest(Paths.get(track.fileAttributes().location())));
            }
        });
        return List.of(menuItem);
    }
}
