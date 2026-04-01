package io.playqd.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.playqd.config.serializer.IntegerPropertySerializer;
import io.playqd.config.serializer.LongPropertySerializer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record PlayerStateProperties(@JsonSerialize(using = IntegerPropertySerializer.class)
                                    IntegerProperty volume,
                                    @JsonSerialize(using = LongPropertySerializer.class)
                                    LongProperty lastPlayedTrackId,
                                    List<Long> tracklist) {

    public PlayerStateProperties() {
        this(0, null, new ArrayList<>());
    }

    @JsonCreator
    public PlayerStateProperties(@JsonProperty("volume") int volume,
                                 @JsonProperty("lastPlayedTrackId") Long lastPlayedTrackId,
                                 @JsonProperty("tracklist") List<Long> tracklist) {
        this(
                new SimpleIntegerProperty(volume),
                new SimpleLongProperty(lastPlayedTrackId != null ? lastPlayedTrackId : -1),
                tracklist != null ? new ArrayList<>(tracklist) : Collections.emptyList());
    }

}
