package io.playqd.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.playqd.config.serializer.BooleanPropertySerializer;
import javafx.beans.property.SimpleBooleanProperty;

public record LibraryProperties(@JsonSerialize(using = BooleanPropertySerializer.class)
                                SimpleBooleanProperty rescanOnStartUp) {

    public LibraryProperties() {
        this(false);
    }

    @JsonCreator
    public LibraryProperties(@JsonProperty("rescanOnStartUp") boolean rescanOnStartUp) {
        this(new SimpleBooleanProperty(rescanOnStartUp));
    }
}
