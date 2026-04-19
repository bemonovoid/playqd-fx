package io.playqd.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.playqd.config.serializer.BooleanPropertySerializer;
import javafx.beans.property.SimpleBooleanProperty;

public record GeneralUIProperties(@JsonSerialize(using = BooleanPropertySerializer.class)
                                  SimpleBooleanProperty confirmExit,
                                  SizeProperties size) {

    public GeneralUIProperties() {
        this(false, new SizeProperties());
    }

    @JsonCreator
    public GeneralUIProperties(@JsonProperty("confirmExit") boolean confirmExit,
                               @JsonProperty("size") SizeProperties size) {
        this(new SimpleBooleanProperty(confirmExit), size);
    }
}
