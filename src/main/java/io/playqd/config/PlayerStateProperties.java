package io.playqd.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;

import io.playqd.config.serializer.DoublePropertySerializer;
import io.playqd.config.serializer.LongPropertySerializer;
import io.playqd.config.serializer.ObjectPropertySerializer;
import io.playqd.player.FetchMode;
import io.playqd.player.PlaybackMode;

public record PlayerStateProperties(@JsonSerialize(using = DoublePropertySerializer.class)
                                    DoubleProperty volume,
                                    @JsonSerialize(using = LongPropertySerializer.class)
                                    LongProperty lastPlayedTrackId,
                                    List<Long> tracklist,
                                    @JsonSerialize(using = ObjectPropertySerializer.class)
                                    ObjectProperty<PlaybackMode> playbackMode,
                                    @JsonSerialize(using = ObjectPropertySerializer.class)
                                    ObjectProperty<FetchMode> fetchMode) {

    public PlayerStateProperties() {
        this(0, null, new ArrayList<>(), PlaybackMode.DEFAULT, FetchMode.NORMAL);
    }

    @JsonCreator
    public PlayerStateProperties(@JsonProperty("volume") double volume,
                                 @JsonProperty("lastPlayedTrackId") Long lastPlayedTrackId,
                                 @JsonProperty("tracklist") List<Long> tracklist,
                                 @JsonProperty("playbackMode") PlaybackMode playbackMode,
                                 @JsonProperty("fetchMode") FetchMode fetchMode) {
        this(
                new SimpleDoubleProperty(volume),
                new SimpleLongProperty(lastPlayedTrackId != null ? lastPlayedTrackId : -1),
                tracklist != null ? new ArrayList<>(tracklist) : Collections.emptyList(),
                new SimpleObjectProperty<>(playbackMode == null ? PlaybackMode.DEFAULT : playbackMode),
                new SimpleObjectProperty<>(fetchMode == null ? FetchMode.NORMAL : fetchMode));
    }

}
