package io.playqd.client;

import io.playqd.data.Album;
import io.playqd.data.Artist;
import io.playqd.data.Track;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class Images {

    private static final Logger LOG = LoggerFactory.getLogger(Images.class);

    private static final String DEFAULT_ARTIST_IMG_URL = "/img/tone.png";
    private static final String DEFAULT_ALBUM_IMG_URL = "/img/no-album-96.png";
    private static final String DEFAULT_AUDIO_FILE_IMG_URL = "/img/audio-file-25.png";

    private static final String DEFAULT_PLAYLIST_IMG_URL = "/img/playlist-40.png";

    private static final Image ALL_ARTIST_IMAGE = new Image("/img/all-artists.png", 25, 25, true, true, true);
    private static final Image DEFAULT_PLAYLIST_IMG = new Image(DEFAULT_PLAYLIST_IMG_URL, 17, 17, true, true, true);
    private static final Image DEFAULT_AUDIO_FILE_IMG = new Image(DEFAULT_AUDIO_FILE_IMG_URL, 19, 19, true, true, true);
    private static final Image DEFAULT_ALBUM_IMG = new Image(DEFAULT_ALBUM_IMG_URL, 19, 19, true, true, true);

    private static final Map<String, Image> ARTIST_IMAGES = new HashMap<>();
    private static final Map<String, Image> IMAGES = new HashMap<>();
    private static final Map<Integer, Image> DEFAULT_ARTIST_IMAGES = new HashMap<>();
    private static final Map<Integer, Image> DEFAULT_ALBUM_IMAGES = new HashMap<>();
    private static final Map<Integer, Image> DEFAULT_AUDIO_FILE_IMAGES = new HashMap<>();

    public static void clearCaches() {
        DEFAULT_ARTIST_IMAGES.clear();
        DEFAULT_ALBUM_IMAGES.clear();
        ARTIST_IMAGES.clear();
        IMAGES.clear();
    }

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
        return IMAGES.computeIfAbsent(albumId + ":" + size, _ -> getImage(PlayqdApis.trackArtwork(albumId), size));
    }

    public static Image album(long trackId, String imageId, int size) {
        return IMAGES.computeIfAbsent(imageId + ":" + size, _ -> getImage(PlayqdApis.trackArtwork(trackId), size));
    }


    public static Image track(long albumId, int size) {
        var key = albumId + ":" + size;
        if (IMAGES.containsKey(key)) {
            var image = IMAGES.get(key);
            if (image.getUrl().contains("no-album")) {
                return DEFAULT_AUDIO_FILE_IMG;
            }
        }
        return album(albumId, size);
    }


    public static Image allArtistsImage() {
        return ALL_ARTIST_IMAGE;
    }

    public static Image getImage(Track track, int size) {
        var key = track.artistName() + "-" + track.albumName() + ":" + size;
        var image = IMAGES.get(key);
        if (image == null) {
            image = new Image(PlayqdApis.trackArtwork(track.id()), size, size, true, true, true);
            image.errorProperty().addListener((_, _, hasError) -> {
               if (hasError != null && hasError) {
                   IMAGES.put(key, DEFAULT_AUDIO_FILE_IMG);
               }
            });
            IMAGES.put(key, image);
        }
        return image;
    }

    public static Image getImage(Album album, int size) {
        var key = album.artistName() + "-" + album.name() + ":" + size;
        var image = IMAGES.get(key);
        if (image == null) {
            image = new Image(PlayqdApis.trackArtwork(album.id()), size, size, true, true, true);
            image.errorProperty().addListener((_, _, hasError) -> {
                if (hasError != null && hasError) {
                    IMAGES.put(key, DEFAULT_AUDIO_FILE_IMG);
                }
            });
            IMAGES.put(key, image);
        }
        return image;
    }

    public static Image getImage(Artist artist, int size) {
        var key = artist.name() + ":" + size;
        var image = IMAGES.get(key);
        if (image == null) {
            image = new Image(PlayqdApis.artistArtwork(artist.id()), size, size, true, true, true);
            image.errorProperty().addListener((_, _, hasError) -> {
                if (hasError != null && hasError) {
                    IMAGES.put(key, DEFAULT_AUDIO_FILE_IMG);
                }
            });
            IMAGES.put(key, image);
        }
        return image;
    }

    public static Image defaultArtist(int size) {
        return DEFAULT_ARTIST_IMAGES.computeIfAbsent(size, _ ->
                new Image(DEFAULT_ARTIST_IMG_URL, size, size, true, true, true));
    }

    public static Image defaultPlaylist(int size) {
        return DEFAULT_PLAYLIST_IMG;
    }

    public static Image defaultAlbum(int size) {
        return DEFAULT_ALBUM_IMAGES.computeIfAbsent(size, _ ->
                new Image(DEFAULT_ALBUM_IMG_URL, size, size, true, true, true));
    }

    public static Image defaultAudioFile(int size) {
        return DEFAULT_AUDIO_FILE_IMAGES.computeIfAbsent(size, _ ->
                new Image(DEFAULT_AUDIO_FILE_IMG_URL, size, size, true, true, true));
    }

    public static Image defaultAlbum() {
        return DEFAULT_ALBUM_IMG;
    }

    public static Image defaultAudioFile() {
        return DEFAULT_AUDIO_FILE_IMG;
    }

    public static void setArtist(long artistId, Image image, int size) {
        ARTIST_IMAGES.put(artistId + ":" + size, image);
    }

    public static void setAlbum(long albumId, Image image, int size) {
        IMAGES.put(albumId + ":" + size, image);
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
