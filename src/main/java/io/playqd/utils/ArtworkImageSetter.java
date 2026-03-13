package io.playqd.utils;

import io.playqd.data.Track;
import io.playqd.service.MusicLibrary;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.Map;

public final class ArtworkImageSetter {

    private static final Map<String, Image> ARTWORK_CACHE = new HashMap<>();

    private static final String ARTWORK_NOT_FOUND_IMAGE_URL = "/img/no-album-art-2.png";

    public static boolean isNotFoundImageUrl(String url) {
        return ARTWORK_NOT_FOUND_IMAGE_URL.equalsIgnoreCase(url);
    }

    public static void set(long trackId, ImageView imageView) {
        set(trackId, -1, imageView);
    }

    public static void set(long trackId, int size, ImageView imageView) {
        set(MusicLibrary.getTrackById(trackId), size, imageView);
    }

    public static void set(Track track, ImageView imageView) {
        set(track, -1, imageView);
    }

    public static void set(Track track, int size, ImageView imageView) {
        var trackId = track.cueInfo().parentId() != null ? track.cueInfo().parentId() : track.id();
        var cacheKey = trackId + "_" + size;
        if (ARTWORK_CACHE.containsKey(cacheKey)) {
            imageView.setImage(ARTWORK_CACHE.get(cacheKey));
            return;
        }
        var url = PlayqdApis.albumArtwork(trackId, size);
        var image = (Image) null;
        try {
            image = new Image(url, size, size, true, true, true);
            imageView.setImage(image);
            ARTWORK_CACHE.put(cacheKey, image);
        } catch (IllegalArgumentException e) {
            imageView.setImage(new Image(ARTWORK_NOT_FOUND_IMAGE_URL, size, size, true, true, true));
            return;
        }
        image.errorProperty().addListener((_, _, hasError) -> {
            if (hasError) {
                imageView.setImage(ARTWORK_CACHE.put(cacheKey,
                        new Image(ARTWORK_NOT_FOUND_IMAGE_URL, size, size, true, true, true)));
            }
        });
    }

    public static Image getImage(Track track) {
        return getImage(track, -1);
    }

    private static Image getImage(Track track, int size) {
        var trackId = track.cueInfo().parentId() != null ? track.cueInfo().parentId() : track.id();
        var cacheKey = trackId + "_" + size;
        if (ARTWORK_CACHE.containsKey(cacheKey)) {
            return ARTWORK_CACHE.get(cacheKey);
        }
        var url = PlayqdApis.albumArtwork(trackId, size);
        var image = (Image) null;
        try {
            image = new Image(url, size, size, true, true, true);
            ARTWORK_CACHE.put(cacheKey, image);
            return image;
        } catch (IllegalArgumentException e) {
            return new Image(ARTWORK_NOT_FOUND_IMAGE_URL, size, size, true, true, true);
        }
    }
}
