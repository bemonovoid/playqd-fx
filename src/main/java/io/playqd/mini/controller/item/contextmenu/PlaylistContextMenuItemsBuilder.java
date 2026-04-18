package io.playqd.mini.controller.item.contextmenu;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.controller.playlists.PlaylistDialog;
import io.playqd.data.PlaylistTrack;
import io.playqd.data.Track;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.util.*;
import java.util.stream.Collectors;

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
                MusicLibrary.removeTracksFromCollection(fromPlaylist, tracks.stream().map(Track::id).toList()));
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
                    menuItem.setOnAction(_ -> {
                        var sameIds = p.tracks().stream()
                                .map(PlaylistTrack::id)
                                .collect(Collectors.toCollection(HashSet::new));
                        var newIds = tracks.stream().map(Track::id).collect(Collectors.toSet());
                        sameIds.retainAll(newIds);
                        if (!sameIds.isEmpty()) {
                            System.out.println("not empty: " + Arrays.toString(sameIds.toArray()));
                        }
                        MusicLibrary.addTracksToPlaylist(p.id(), tracks.stream().map(Track::id).toList());
                    });
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
