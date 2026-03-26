package io.playqd.utils;

import io.playqd.config.AppConfig;

public class PlayqdApis {

    public static String baseUrl() {
        return AppConfig.getProperties().serverHost().get();
    }

    public static String trackStream(long id) {
        return String.format("%s/tracks/%s/file", baseUrl(), id);
    }

    public static String watchFolderItemBinary(String id) {
        return String.format("%s/folders/items/%s/binary", baseUrl(), id);
    }

    public static String albumArtwork(long trackId) {
        return baseUrl() + "/tracks/" + trackId + "/artwork";
    }

}
