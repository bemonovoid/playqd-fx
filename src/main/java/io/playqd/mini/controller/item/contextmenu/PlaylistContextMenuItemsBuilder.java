package io.playqd.mini.controller.item.contextmenu;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.controller.playlists.PlaylistDialog;
import io.playqd.data.Track;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class PlaylistContextMenuItemsBuilder {

    private final List<Track> tracks;
    private final List<MenuItem> menuItems = new ArrayList<>();

    private PlaylistContextMenuItemsBuilder(List<Track> tracks) {
        this.tracks = tracks;
    }

    public static PlaylistContextMenuItemsBuilder newBuilder(List<Track> tracks) {
        return new PlaylistContextMenuItemsBuilder(tracks);
    }

    public static List<MenuItem> buildDefault(List<Track> tracks) {
        return newBuilder(tracks).addToPlaylistMenu(-1).build();
    }

    public PlaylistContextMenuItemsBuilder addToPlaylistMenu(long excludingPlaylistId) {

        var addToPlaylistMenu = new Menu("Add to playlist", new FontAwesomeIconView(FontAwesomeIcon.HEADPHONES));

        var newPlaylist = new MenuItem("<<New playlist>>");
        newPlaylist.setOnAction(_ -> {
            var dialog = new PlaylistDialog();
            dialog.showAndWait().ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    var trackIds = this.tracks.stream().map(Track::id).toList();
                    MusicLibrary.createPlaylist(name, trackIds);
                }
            });
        });
        addToPlaylistMenu.getItems().add(newPlaylist);
        addToPlaylistMenu.getItems().add(new SeparatorMenuItem());
        addToPlaylistMenu.getItems().addAll(addToPlaylistsMenuItems(excludingPlaylistId));

        menuItems.add(addToPlaylistMenu);
        return this;
    }

    public PlaylistContextMenuItemsBuilder moveToPlaylistMenu(long excludingPlaylistId) {
        var moveToPlaylistMenu = new Menu("Move to playlist", new FontAwesomeIconView(FontAwesomeIcon.HEADPHONES));
        moveToPlaylistMenu.getItems().addAll(moveToPlaylistsMenuItems(excludingPlaylistId));
        menuItems.add(moveToPlaylistMenu);
        return this;
    }

    public PlaylistContextMenuItemsBuilder removeMenuItem(long fromPlaylist) {
        var removeMenuItem = new MenuItem("Remove from playlist");
        removeMenuItem.setOnAction(_ ->
                MusicLibrary.removeTracksFromPlaylist(fromPlaylist, tracks.stream().map(Track::id).toList()));
        return this;
    }

    public List<MenuItem> build() {
        return Collections.unmodifiableList(menuItems);
    }

    private List<MenuItem> addToPlaylistsMenuItems(long excludingPlaylistId) {
        return MusicLibrary.getPlaylists().stream()
                .filter(p -> p.id() != excludingPlaylistId)
                .map(p -> {
                    var menuItem = new MenuItem(p.name());
                    menuItem.setOnAction(_ ->
                            MusicLibrary.addTracksToPlaylist(p.id(), tracks.stream().map(Track::id).toList()));
                    return menuItem;
                })
                .toList();
    }

    private List<MenuItem> moveToPlaylistsMenuItems(long fromPlaylist) {
        return MusicLibrary.getPlaylists().stream()
                .filter(p -> p.id() != fromPlaylist)
                .map(p -> {
                    var menuItem = new MenuItem(p.name());
                    menuItem.setOnAction(_ ->
                            MusicLibrary.movePlaylistTracks(fromPlaylist, p.id(), tracks.stream().map(Track::id).toList()));
                    return menuItem;
                })
                .toList();
    }
}
