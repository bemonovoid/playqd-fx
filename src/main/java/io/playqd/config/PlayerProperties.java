package io.playqd.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlayerProperties(@JsonProperty("state") PlayerStateProperties state,
                               @JsonProperty("dbus") DBusProperties dbus) {

    public PlayerProperties() {
        this(new PlayerStateProperties(), new DBusProperties());
    }

}
