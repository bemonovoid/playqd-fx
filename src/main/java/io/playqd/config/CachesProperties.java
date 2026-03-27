package io.playqd.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.playqd.config.serializer.BooleanPropertySerializer;
import javafx.beans.property.SimpleBooleanProperty;

public record CachesProperties(@JsonSerialize(using = BooleanPropertySerializer.class)
                               SimpleBooleanProperty invalidateClient,
                               @JsonSerialize(using = BooleanPropertySerializer.class)
                               SimpleBooleanProperty invalidateAll) {

    public CachesProperties() {
        this(true, false);
    }

    @JsonCreator
    public CachesProperties(@JsonProperty("invalidateClient") boolean invalidateClient,
                            @JsonProperty("invalidateAll") boolean invalidateAll) {
        this(
                new SimpleBooleanProperty(invalidateClient),
                new SimpleBooleanProperty(invalidateAll)
        );
    }
}
