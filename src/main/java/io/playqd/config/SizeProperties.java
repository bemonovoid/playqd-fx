package io.playqd.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.playqd.config.serializer.DoublePropertySerializer;
import javafx.beans.property.SimpleDoubleProperty;

public record SizeProperties(@JsonSerialize(using = DoublePropertySerializer.class)
                             SimpleDoubleProperty height,
                             @JsonSerialize(using = DoublePropertySerializer.class)
                             SimpleDoubleProperty width) {

    public SizeProperties() {
        this(0, 0);
    }

    @JsonCreator
    public SizeProperties(@JsonProperty("height") double height, @JsonProperty("width") double width) {
        this(new SimpleDoubleProperty(height), new SimpleDoubleProperty(width));
    }
}
