package io.playqd.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.playqd.config.serializer.DoublePropertySerializer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ColumnProperties(@JsonSerialize(using = DoublePropertySerializer.class)
                               DoubleProperty nameCol,
                               @JsonSerialize(using = DoublePropertySerializer.class)
                               DoubleProperty descriptionCol) {

    public ColumnProperties() {
        this(0, 0);
    }

    @JsonCreator
    public ColumnProperties(@JsonProperty("nameCol") double nameCol,
                            @JsonProperty("descriptionCol") double descriptionCol) {
        this(new SimpleDoubleProperty(nameCol), new SimpleDoubleProperty(descriptionCol));
    }

}
