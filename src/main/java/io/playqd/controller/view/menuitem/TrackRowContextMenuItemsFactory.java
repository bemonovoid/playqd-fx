package io.playqd.controller.view.menuitem;

import io.playqd.data.NewMediaCollectionItem;
import io.playqd.data.Playlist;
import io.playqd.data.Track;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TrackRowContextMenuItemsFactory {

    private Supplier<Playlist> thisPlaylist;
    private Consumer<Playlist> playlistModifiedCallback;

    public List<MenuItem> get(List<Track> tracks) {
        var playMenuItems = new PlayMenuItems().onPlay(() -> tracks).build();
        var favoritesMenuItems = new ReactionsMenuItems().setOnAction(() -> tracks).build();
        var playlistMenuItems = new PlaylistMenuItems()
                .setThisPlaylist(thisPlaylist)
                .setPlaylistModifiedCallback(playlistModifiedCallback)
                .setSelectedTracks(tracks)
                .build();
        var collectionMenuItems = new CollectionsMenuItems()
                .onAddItemsToCollection(() -> NewMediaCollectionItem.create(tracks))
                .build();
        var showInFolderItems = new ShowInFolderItems(() -> {
            var track = tracks.getFirst();
            if (track.isCueTrack()) {
                track = MusicLibrary.getTrackById(track.parentId());
            }
            return Paths.get(track.fileAttributes().location());
        }).build();
        var shoArtworkGalleryItems = new ShowArtworkGalleryItems(tracks::getFirst).build();

        var items = new ArrayList<>(playMenuItems);

        items.add(new SeparatorMenuItem());
        items.addAll(favoritesMenuItems);
        items.add(new SeparatorMenuItem());
        items.addAll(playlistMenuItems);
        items.addAll(collectionMenuItems);
        items.add(new SeparatorMenuItem());
        items.addAll(showInFolderItems);
        items.addAll(shoArtworkGalleryItems);

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

    public void setThisPlaylist(Supplier<Playlist> thisPlaylist) {
        this.thisPlaylist = thisPlaylist;
    }

    public void setPlaylistModifiedCallback(Consumer<Playlist> playlistModifiedCallback) {
        this.playlistModifiedCallback = playlistModifiedCallback;
    }
}
