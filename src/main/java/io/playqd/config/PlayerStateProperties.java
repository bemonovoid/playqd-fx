package io.playqd.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;

import io.playqd.config.serializer.DoublePropertySerializer;
import io.playqd.config.serializer.LongPropertySerializer;

public record PlayerStateProperties(@JsonSerialize(using = DoublePropertySerializer.class)
                                    DoubleProperty volume,
                                    @JsonSerialize(using = LongPropertySerializer.class)
                                    LongProperty lastPlayedTrackId,
                                    List<Long> tracklist) {

    public PlayerStateProperties() {
        this(0, null, new ArrayList<>());
    }

    @JsonCreator
    public PlayerStateProperties(@JsonProperty("volume") double volume,
                                 @JsonProperty("lastPlayedTrackId") Long lastPlayedTrackId,
                                 @JsonProperty("tracklist") List<Long> tracklist) {
        this(
                new SimpleDoubleProperty(volume),
                new SimpleLongProperty(lastPlayedTrackId != null ? lastPlayedTrackId : -1),
                tracklist != null ? new ArrayList<>(tracklist) : Collections.emptyList());
    }

}
