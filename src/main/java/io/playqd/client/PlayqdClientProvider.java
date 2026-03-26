package io.playqd.client;

import io.playqd.config.AppConfig;

public class PlayqdClientProvider {

    private static PlayqdClient PLAYQD_CLIENT;

    static {
        PLAYQD_CLIENT = initialize();
        AppConfig.getProperties().serverHost().addListener((_, _, newVal) -> {

        });
    }

    public static PlayqdClient get() {
        return PLAYQD_CLIENT;
    }

    private static PlayqdClient initialize() {
        return PlayqdClient.builder()
                .serverHost(AppConfig.getProperties().serverHost().get())
                .build();
    }
}
