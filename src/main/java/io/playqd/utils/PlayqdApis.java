package io.playqd.utils;

import io.playqd.config.AppConfig;

public class PlayqdApis {

    public static String baseUrl() {
        return AppConfig.getProperties().apiBaseUrl().get();
    }

}
