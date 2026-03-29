package io.playqd.utils;

import io.playqd.data.MediaCollectionItem;
import io.playqd.data.MediaItemType;
import io.playqd.data.Track;
import io.playqd.data.WatchFolderItem;

import java.util.List;

public final class MediaCollectionUtils {

    public static MediaCollectionItem buildCollectionItem(WatchFolderItem item) {
        return new MediaCollectionItem(-1, item.name(), MediaItemType.FILE, item.location(), "");
    }

    public static MediaCollectionItem buildAlbumArtworkItem(Track track) {
        return new MediaCollectionItem(
                -1, track.artistName() + " - " + track.title(), MediaItemType.ARTWORK, "" + track.id(), "");
    }

    public static List<MediaCollectionItem> buildTrackItems(List<Track> tracks) {
        return tracks.stream().map(MediaCollectionUtils::buildTrackItem).toList();
    }

    public static MediaCollectionItem buildTrackItem(Track track) {
        return new MediaCollectionItem(
                -1,
                track.artistName() + " - " + track.title(),
                MediaItemType.TRACK,
                "" + track.id(),
                "");
    }
}
