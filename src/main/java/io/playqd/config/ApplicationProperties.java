package io.playqd.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.playqd.config.serializer.StringPropertySerializer;
import javafx.beans.property.SimpleStringProperty;

public record ApplicationProperties(@JsonSerialize(using = StringPropertySerializer.class)
                                    SimpleStringProperty apiBaseUrl,
                                    UIProperties ui) {

    @JsonCreator
    public ApplicationProperties(@JsonProperty("apiBaseUrl") String apiBaseUrl,
                                 @JsonProperty("ui") UIProperties ui) {
        this(new SimpleStringProperty(apiBaseUrl), ui);
    }

    public ApplicationProperties() {
        this("http://localhost:8016/api/v1", new UIProperties());
    }

}
