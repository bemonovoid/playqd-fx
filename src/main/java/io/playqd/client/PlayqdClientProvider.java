package io.playqd.client;

import io.playqd.config.AppConfig;

import java.util.concurrent.atomic.AtomicReference;

public class PlayqdClientProvider {

    private static final AtomicReference<PlayqdClient> PLAYQD_CLIENT = new AtomicReference<>();

    static {
        PLAYQD_CLIENT.set(initialize());
        AppConfig.getProperties().serverHost().addListener((_, _, _) -> PLAYQD_CLIENT.set(initialize()));
    }

    public static PlayqdClient get() {
        return PLAYQD_CLIENT.get();
    }

    private static PlayqdClient initialize() {
        return PlayqdClient.builder()
                .serverHost(AppConfig.getProperties().serverHost().get())
                .build();
    }
}
