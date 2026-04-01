package io.playqd.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.playqd.config.serializer.IntegerPropertySerializer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public record PlayerProperties(@JsonSerialize(using = IntegerPropertySerializer.class) IntegerProperty volume,
                               DBusProperties dbus) {

    public PlayerProperties() {
        this(0, new DBusProperties());
    }

    @JsonCreator
    public PlayerProperties(@JsonProperty("volume") int volume, @JsonProperty("dbus") DBusProperties dbus) {
        this(new SimpleIntegerProperty(volume), dbus);
    }
}
