package io.playqd.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.playqd.config.serializer.BooleanPropertySerializer;
import javafx.beans.property.SimpleBooleanProperty;

public record UIProperties(@JsonSerialize(using = BooleanPropertySerializer.class) SimpleBooleanProperty confirmExit) {

    public UIProperties() {
        this(false);
    }

    @JsonCreator
    public UIProperties(@JsonProperty("confirmExit") boolean confirmExit) {
        this(new SimpleBooleanProperty(confirmExit));
    }

}
