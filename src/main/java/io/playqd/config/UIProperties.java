package io.playqd.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UIProperties(@JsonProperty("app") GeneralUIProperties app) {

    public UIProperties() {
        this(new GeneralUIProperties());
    }

}
