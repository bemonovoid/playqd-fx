package io.playqd.controller.view.menuitem;

import io.playqd.data.PlaylistWithTrackIds;
import io.playqd.data.Track;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TrackRowContextMenuItemsFactory {

    private Supplier<PlaylistWithTrackIds> thisPlaylist;
    private Consumer<PlaylistWithTrackIds> playlistModifiedCallback;

    public List<MenuItem> get(List<Track> tracks) {
        var playMenuItems = new PlayMenuItems().onPlay(() -> tracks).build();
        var favoritesMenuItems = new FavoritesMenuItems().setOnAction(() -> tracks).build();
        var playlistMenuItems = new PlaylistMenuItems()
                .setThisPlaylist(thisPlaylist)
                .setPlaylistModifiedCallback(playlistModifiedCallback)
                .setSelectedTracks(tracks)
                .build();
        var collectionMenuItems = new CollectionsMenuItems().build();
        var showInFolderItems = new ShowInFolderItems().setOnAction(() -> tracks).build();

        var items = new ArrayList<>(playMenuItems);

        items.add(new SeparatorMenuItem());
        items.addAll(favoritesMenuItems);
        items.add(new SeparatorMenuItem());
        items.addAll(playlistMenuItems);
        items.addAll(collectionMenuItems);
        items.add(new SeparatorMenuItem());
        items.addAll(showInFolderItems);

        var cueFileTracks = tracks.stream()
                .filter(Track::isCueTrack)
                .map(t -> MusicLibrary.getTrackById(t.realId()))
                .toList();
        if (!cueFileTracks.isEmpty()) {
            var cueMenuItems = new CueMenuItems().setSelectedTracks(cueFileTracks).build();
            items.addAll(cueMenuItems);
        }

        return items;
    }

    public void setThisPlaylist(Supplier<PlaylistWithTrackIds> thisPlaylist) {
        this.thisPlaylist = thisPlaylist;
    }

    public void setPlaylistModifiedCallback(Consumer<PlaylistWithTrackIds> playlistModifiedCallback) {
        this.playlistModifiedCallback = playlistModifiedCallback;
    }
}
