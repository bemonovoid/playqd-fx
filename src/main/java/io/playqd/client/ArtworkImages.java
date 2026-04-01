package io.playqd.client;

import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class ArtworkImages {

    private static final Logger LOG = LoggerFactory.getLogger(ArtworkImages.class);

    private static final String DEFAULT_ARTIST_IMG_URL = "/img/tone.png";
    private static final String DEFAULT_ALBUM_IMG_URL = "/img/no-album-art-3.png";

    private static final Image ALL_ARTIST_IMAGE = new Image("/img/all-artists.png", 25, 25, true, true, true);

    private static final Map<String, Image> ARTIST_IMAGES = new HashMap<>();
    private static final Map<String, Image> ALBUM_IMAGES = new HashMap<>();
    private static final Map<Integer, Image> DEFAULT_ARTIST_IMAGES = new HashMap<>();
    private static final Map<Integer, Image> DEFAULT_ALBUM_IMAGES = new HashMap<>();

    public static Image artist(long artistId) {
        return getImage(PlayqdApis.artistArtwork(artistId), -1);
    }

    public static Image artist(long artistId, int size) {
        return ARTIST_IMAGES.computeIfAbsent(
                artistId + ":" + size, _ -> getImage(PlayqdApis.artistArtwork(artistId), size));
    }

    public static Image album(long albumId) {
        return getImage(PlayqdApis.trackArtwork(albumId), -1);
    }

    public static Image album(long albumId, int size) {
        return ALBUM_IMAGES.computeIfAbsent(albumId + ":" + size, _ -> getImage(PlayqdApis.trackArtwork(albumId), size));
    }

    public static Image allArtistsImage() {
        return ALL_ARTIST_IMAGE;
    }

    public static Image defaultArtist(int size) {
        return DEFAULT_ARTIST_IMAGES.computeIfAbsent(size, _ ->
                new Image(DEFAULT_ARTIST_IMG_URL, size, size, true, true, true));
    }

    public static Image defaultAlbum(int size) {
        return DEFAULT_ALBUM_IMAGES.computeIfAbsent(size, _ ->
                new Image(DEFAULT_ALBUM_IMG_URL, size, size, true, true, true));
    }

    public static void setArtist(long artistId, Image image, int size) {
        ARTIST_IMAGES.put(artistId + ":" + size, image);
    }

    public static void setAlbum(long albumId, Image image, int size) {
        ALBUM_IMAGES.put(albumId + ":" + size, image);
    }

    public static boolean isDefaultImage(String url) {
        return DEFAULT_ALBUM_IMG_URL.equalsIgnoreCase(url) || DEFAULT_ARTIST_IMG_URL.equalsIgnoreCase(url);
    }

    private static Image getImage(String url, int size) {
        var image = (Image) null;
        try {
            image = new Image(url, size, size, true, true, true);
            return image;
        } catch (IllegalArgumentException e) {
            LOG.error("Get image failed. {}. {}", url, e.getMessage(), e);
            return null;
        }
    }
}
