package io.playqd.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.playqd.config.serializer.BooleanPropertySerializer;
import javafx.beans.property.SimpleBooleanProperty;

public record LibraryProperties(@JsonSerialize(using = BooleanPropertySerializer.class)
                                SimpleBooleanProperty rescanOnStartUp,
                                CachesProperties caches) {

    public LibraryProperties() {
        this(false, new CachesProperties());
    }

    @JsonCreator
    public LibraryProperties(@JsonProperty("rescanOnStartUp") boolean rescanOnStartUp,
                             @JsonProperty("caches") CachesProperties caches) {
        this(new SimpleBooleanProperty(rescanOnStartUp), caches);
    }
}
