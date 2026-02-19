package io.playqd.utils;

import io.playqd.config.AppConfig;

public class PlayqdApis {

    public static String baseUrl() {
        return AppConfig.getProperties().apiBaseUrl().get();
    }

    public static String albumArtwork(String albumId) {
        return baseUrl() + "/albums/" + albumId + "/artwork";
    }

}
