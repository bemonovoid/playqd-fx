package io.playqd.controller.view.menuitem;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import io.playqd.controller.playlists.PlaylistDialog;
import io.playqd.data.Track;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class TrackContextMenuConfigurer {

    protected final PlayGroupConfigurer playGroupConfigurer;
    protected final PlaylistGroupConfigurer playlistGroupConfigurer;

    public TrackContextMenuConfigurer() {
        this.playGroupConfigurer = buildPlayGroupConfigurer();
        this.playlistGroupConfigurer = buildPlaylistGroupConfigurer();
    }

    public List<MenuItem> configure(List<Track> selectedTracks) {
        var playGroupItems = playGroupConfigurer.configure(selectedTracks);
        var playlistGroupItems = playlistGroupConfigurer.configure(selectedTracks);
        var allItems = new ArrayList<MenuItem>(playGroupItems.size() + playlistGroupItems.size());
        allItems.addAll(playGroupItems);
        allItems.addAll(playlistGroupItems);
        return allItems;
    }

    private static PlayGroupConfigurer buildPlayGroupConfigurer() {
        return new PlayGroupConfigurer(
                new MenuItemConfigurer(
                        "Play now",
                        () -> FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.PLAY)),
                new MenuItemConfigurer(
                        "Queue next"),
                new MenuItemConfigurer(
                        "Queue last")
        );
    }

    private static PlaylistGroupConfigurer buildPlaylistGroupConfigurer() {
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
