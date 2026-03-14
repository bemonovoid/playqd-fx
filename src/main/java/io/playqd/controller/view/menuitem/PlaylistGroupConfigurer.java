package io.playqd.controller.view.menuitem;

import io.playqd.controller.playlists.PlaylistDialog;
import io.playqd.data.Track;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public record PlaylistGroupConfigurer(MenuConfigurer addToPlaylist,
                                      MenuConfigurer moveToPlaylist,
                                      MenuItemConfigurer removeFromPlaylist) implements GroupConfigurer {
    @Override
    public List<MenuItem> configure(List<Track> selectedTracks) {

        return Stream.of(addToPlaylist, moveToPlaylist, removeFromPlaylist)
                .filter(c -> !c.isExcluded())
                .map(c -> c.configure(selectedTracks))
                .toList();
    }

    static PlaylistGroupConfigurer build() {
        return new PlaylistGroupConfigurer(
                buildAddToPlaylistMenuConfigurer(),
                buildMoveToPlaylistMenuConfigurer(),
                buildRemoveFromPlaylistConfigurer()
        );
    }

    private static MenuConfigurer buildAddToPlaylistMenuConfigurer() {
        var configurer = new MenuConfigurer("Add to playlist", playlistMenuItemConfigurers());
        return configurer;
    }

    private static MenuConfigurer buildMoveToPlaylistMenuConfigurer() {
        var configurer = new MenuConfigurer("Move to playlist",
                List.of(buildAddNewPlaylistConfigurer(), new SeparatorMenuItemConfigurer()));
        configurer.setExcluded(true); // is only included by PlaylistTrackContextMenuConfigurer
        return configurer;
    }

    private static List<MenuItemConfigurer> playlistMenuItemConfigurers() {
        var playlistConfigurers = MusicLibrary.getPlaylists().stream()
                .map(pl -> new MenuItemConfigurer(
                        pl.name(),
                        tracks -> MusicLibrary.addTracksToPlaylist(pl.id(), tracks.stream().map(Track::id).toList())))
                .toList();
        var result = new ArrayList<MenuItemConfigurer>( playlistConfigurers.size() + 1);
        result.add(buildAddNewPlaylistConfigurer());
        result.add(new SeparatorMenuItemConfigurer());
        result.addAll( playlistConfigurers);
        return result;
    }
    private static MenuItemConfigurer buildAddNewPlaylistConfigurer() {
        return new MenuItemConfigurer("<<New playlist>>", tracks -> {
            var dialog = new PlaylistDialog();
            dialog.showAndWait().ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    var trackIds = tracks.stream().map(Track::id).toList();
                    MusicLibrary.createPlaylist(name, trackIds);
                }
            });
        });
    }

    private static MenuItemConfigurer buildRemoveFromPlaylistConfigurer() {
        var configurer = new MenuItemConfigurer("Remove from playlist");
        configurer.setExcluded(true); // is only included by PlaylistTrackContextMenuConfigurer
        return configurer;
    }
}
