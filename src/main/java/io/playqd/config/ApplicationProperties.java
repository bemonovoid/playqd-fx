package io.playqd.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.playqd.config.serializer.StringPropertySerializer;
import javafx.beans.property.SimpleStringProperty;

public record ApplicationProperties(@JsonSerialize(using = StringPropertySerializer.class)
                                    SimpleStringProperty serverHost,
                                    UIProperties ui,
                                    LibraryProperties library) {

    @JsonCreator
    public ApplicationProperties(@JsonProperty("serverHost") String serverHost,
                                 @JsonProperty("ui") UIProperties ui,
                                 @JsonProperty("library") LibraryProperties library) {
        this(new SimpleStringProperty(serverHost), ui, library);
    }

    public ApplicationProperties() {
        this("http://localhost:8016", new UIProperties(), new LibraryProperties());
    }

}
