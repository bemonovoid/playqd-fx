package io.playqd.client;

import io.playqd.config.AppConfig;

public class PlayqdApis {

    public static String baseUrl() {
        return AppConfig.getProperties().serverHost().get();
    }

    public static String trackStream(long id) {
        return String.format("%s/api/v1/tracks/%s/stream", baseUrl(), id);
    }

    public static String artistArtwork(long trackId) {
        return String.format("%s/api/v1/artists/%s/artwork", baseUrl(), trackId);
    }

    public static String trackArtwork(long trackId) {
        return String.format("%s/api/v1/tracks/%s/artwork", baseUrl(), trackId);
    }

    public static String trackCueFile(long trackId) {
        return String.format("%s/api/v1/tracks/%s/cue", baseUrl(), trackId);
    }

    public static String watchFolderItemBinary(String id) {
        return String.format("%s/api/v1/folders/items/%s/binary", baseUrl(), id);
    }

}
