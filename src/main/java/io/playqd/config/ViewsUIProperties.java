package io.playqd.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ViewsUIProperties(@JsonProperty ColumnProperties tracks) {

    public ViewsUIProperties() {
        this(new ColumnProperties());
    }
}
