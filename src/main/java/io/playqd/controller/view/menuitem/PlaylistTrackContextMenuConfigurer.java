package io.playqd.controller.view.menuitem;

import io.playqd.controller.playlists.PlaylistsViewController;
import io.playqd.data.Track;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.MenuItem;

import java.util.List;
import java.util.function.Consumer;

public class PlaylistTrackContextMenuConfigurer extends TrackContextMenuConfigurer {

    private final PlaylistsViewController playlistsViewController;

    public PlaylistTrackContextMenuConfigurer(PlaylistsViewController playlistsViewController) {
        this.playlistsViewController = playlistsViewController;
    }

    @Override
    public List<MenuItem> configure(List<Track> selectedTracks) {
        var selectedPlaylist = playlistsViewController.getSelectedPlaylist();
        var selectedPlaylistIndex = playlistsViewController.getSelectedPlaylistIndex();

        playlistGroupConfigurer.addToPlaylist().setText("Copy to playlist");
        playlistGroupConfigurer.addToPlaylist().itemsConfigurers().stream()
                .filter(menuItemConfigurer -> menuItemConfigurer.text().equals(selectedPlaylist.name()))
                .findFirst()
                .ifPresent(menuItemConfigurer -> menuItemConfigurer.setExcluded(true));

        playlistGroupConfigurer.moveToPlaylist().setExcluded(false);
        var moveToPlaylistConfigurers = MusicLibrary.getPlaylists().stream()
                .filter(pl -> pl.id() != selectedPlaylist.id())
                .map(pl -> new MenuItemConfigurer(
                        pl.name(),
                        tracks -> {
                            MusicLibrary.movePlaylistTracks(
                                    selectedPlaylist.id(), pl.id(), tracks.stream().map(Track::id).toList());
                            playlistsViewController.refreshPlaylistAtIndex(selectedPlaylistIndex);
                        }))
                .toList();
        playlistGroupConfigurer.moveToPlaylist().addMenuItemConfigurers(moveToPlaylistConfigurers);

        playlistGroupConfigurer.removeFromPlaylist().setExcluded(false);
        playlistGroupConfigurer.removeFromPlaylist().addOnAction(
                onRemoveFromPlaylist(selectedPlaylist.id(), selectedPlaylistIndex));

        return super.configure(selectedTracks);
    }

    private Consumer<List<Track>> onRemoveFromPlaylist(long playlistId, int playlistIndex) {
        return tracks -> {
            MusicLibrary.removeTracksFromPlaylist(playlistId, tracks.stream().map(Track::id).toList());
            playlistsViewController.refreshPlaylistAtIndex(playlistIndex);
        };
    }
}
