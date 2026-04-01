package io.playqd.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.playqd.config.serializer.BooleanPropertySerializer;
import javafx.beans.property.SimpleBooleanProperty;

public record DBusProperties(@JsonSerialize(using = BooleanPropertySerializer.class)
                             SimpleBooleanProperty enabled) {

    public DBusProperties() {
        this(false);
    }

    @JsonCreator
    public DBusProperties(@JsonProperty("enabled") boolean enabled) {
        this(new SimpleBooleanProperty(enabled));
    }
}
