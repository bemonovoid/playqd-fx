package io.playqd.client;

import io.playqd.config.AppConfig;

public class PlayqdClientProvider {

    private static final PlayqdClient PLAYQD_CLIENT;

    static {
        PLAYQD_CLIENT = PlayqdClient.builder()
                .apiBaseUrl(AppConfig.getProperties().apiBaseUrl().get())
                .build();
    }

    public static PlayqdClient get() {
        return PLAYQD_CLIENT;
    }
}
