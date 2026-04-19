package io.playqd.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UIProperties(@JsonProperty("app") GeneralUIProperties app,
                           @JsonProperty("views") ViewsUIProperties views) {

    public UIProperties() {
        this(new GeneralUIProperties(), new ViewsUIProperties());
    }

}
