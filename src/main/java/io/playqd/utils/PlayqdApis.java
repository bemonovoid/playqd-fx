package io.playqd.utils;

import io.playqd.config.AppConfig;

public class PlayqdApis {

    public static String baseUrl() {
        return AppConfig.getProperties().apiBaseUrl().get();
    }

    public static String albumArtwork(long trackId) {
        return albumArtwork(trackId, -1);
    }

    public static String albumArtwork(long trackId, int size) {
        var url = baseUrl() + "/tracks/" + trackId + "/artwork";

        if (size > 0) {
            url = url + "?" + size;
        }

        return url;
    }

}
