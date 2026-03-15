package io.playqd.controller.view;

import io.playqd.data.Track;
import io.playqd.utils.DateUtils;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public record TrackModel(Track track,
                         IntegerProperty playCount,
                         StringProperty lastPlayedDisplayDate,
                         IntegerProperty rating,
                         StringProperty ratedDisplayDate,
                         String fileDisplaySize,
                         String addedToWatchFolderDisplayDate) implements TableModelProperties {

    public TrackModel(Track track) {
        this(
                track,
                new SimpleIntegerProperty(track.playback().count()),
                new SimpleStringProperty(track.playback().lastPlayedDate() == null ? "" :
                        DateUtils.ldtFormatted(track.playback().lastPlayedDate())),
                new SimpleIntegerProperty(track.rating().value()),
                new SimpleStringProperty(track.rating().ratedDate() == null ? "" :
                        DateUtils.ldtFormatted(track.rating().ratedDate())),
                track.fileAttributes().size() == 0 ? "" : track.fileAttributes().readableSize(),
                track.playback().lastPlayedDate() == null ? "" :
                        DateUtils.ldtFormatted(track.additionalInfo().addedToWatchFolderDate())
        );
    }

    @Override
    public IntegerProperty getPlayCount() {
        return playCount();
    }

    @Override
    public IntegerProperty getRating() {
        return rating();
    }

    @Override
    public StringProperty getLastPlayedDisplayDate() {
        return lastPlayedDisplayDate();
    }

    @Override
    public StringProperty getRatedDisplayDate() {
        return ratedDisplayDate();
    }

    public void setObservableProperties(Track track) {
        if (track().rating().value() != track.rating().value()) {
            rating.set(track.rating().value());
            ratedDisplayDate.set(DateUtils.ldtFormatted(track.rating().ratedDate()));
        }
    }
}
