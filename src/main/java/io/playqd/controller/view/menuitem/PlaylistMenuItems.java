package io.playqd.controller.view.menuitem;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.controller.playlists.PlaylistDialog;
import io.playqd.data.Playlist;
import io.playqd.data.Track;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PlaylistMenuItems implements MenuItemsBuilder {

    private List<Track> selectedTracks = Collections.emptyList();
    private Supplier<Playlist> thisPlaylistSupplier = () -> null;
    private Consumer<Playlist> playlistModifiedCallback = pl -> {};

    public PlaylistMenuItems setSelectedTracks(List<Track> selectedTracks) {
        if (selectedTracks != null) {
            this.selectedTracks = selectedTracks;
        }
        return this;
    }

    public PlaylistMenuItems setThisPlaylist(Supplier<Playlist> thisPlaylistSupplier) {
        if (thisPlaylistSupplier != null) {
            this.thisPlaylistSupplier = thisPlaylistSupplier;
        }
        return this;
    }

    public PlaylistMenuItems setPlaylistModifiedCallback(Consumer<Playlist> playlistModifiedCallback) {
        if (playlistModifiedCallback != null) {
            this.playlistModifiedCallback = playlistModifiedCallback;
        }
        return this;
    }

    @Override
    public List<MenuItem> build() {
        var addMenu = new Menu("Add to playlist", new FontAwesomeIconView(FontAwesomeIcon.HEADPHONES));

        var newMenuItem = new MenuItem("<<New playlist>>");
        newMenuItem.setOnAction(_ -> {
            var dialog = new PlaylistDialog();
            dialog.showAndWait().ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    var trackIds = selectedTracks.stream().map(Track::id).toList();
                    MusicLibrary.createPlaylist(name, trackIds);
                }
            });
        });

        addMenu.getItems().add(newMenuItem);
        addMenu.getItems().add(new SeparatorMenuItem());
        addMenu.getItems().addAll(buildPlaylistItemMenuItems(pl ->
                MusicLibrary.addTracksToPlaylist(pl.id(), selectedTracks.stream().map(Track::id).toList())));

        var menuItems = new ArrayList<MenuItem>();
        menuItems.add(addMenu);

        if (thisPlaylistSupplier.get() != null) {
            var moveToMenuItem = new Menu("Move to playlist");
            moveToMenuItem.getItems().addAll(buildPlaylistItemMenuItems(pl -> {
                MusicLibrary.movePlaylistTracks(
                        thisPlaylistSupplier.get().id(), pl.id(), selectedTracks.stream().map(Track::id).toList());
                if (playlistModifiedCallback != null) {
                    playlistModifiedCallback.accept(thisPlaylistSupplier.get());
                }
            }));
            menuItems.add(moveToMenuItem);

            var removeMenuItem = new MenuItem("Remove from playlist");
            removeMenuItem.setOnAction(_ -> {
                var updatePlaylist = MusicLibrary.removeTracksFromPlaylist(
                        thisPlaylistSupplier.get().id(), selectedTracks.stream().map(Track::id).toList());
                if (playlistModifiedCallback != null) {
                    playlistModifiedCallback.accept(updatePlaylist);
                }
            });
            menuItems.add(removeMenuItem);
        }
        return menuItems;
    }

    private List<MenuItem> buildPlaylistItemMenuItems(Consumer<Playlist> playlistConsumer) {
        return MusicLibrary.getPlaylists().stream()
                .filter(pl -> {
                    var thisPlaylist = thisPlaylistSupplier.get();
                    if (thisPlaylist != null) {
                        return !pl.name().equals(thisPlaylist.name());
                    }
                    return true;
                })
                .map(pl -> {
                    var mi = new MenuItem(pl.name());
                    mi.setOnAction(_ -> playlistConsumer.accept(pl));
                    return mi;
                })
                .toList();
    }

}
