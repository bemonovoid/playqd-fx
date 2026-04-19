package io.playqd.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SizesProperties(@JsonProperty("app") SizeProperties app,
                              @JsonProperty("columns") ColumnProperties columns) {

    public SizesProperties() {
        this(new SizeProperties(), new ColumnProperties());
    }
}
