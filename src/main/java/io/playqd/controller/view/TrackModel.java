package io.playqd.controller.view;

import io.playqd.data.Track;
import io.playqd.utils.DateUtils;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TrackModel(long id,
                         String title,
                         String number,
                         String comment,
                         String lyrics,
                         String artistName,
                         String albumName,
                         String genre,
                         String releaseDate,
                         int length,
                         String displayLength,
                         IntegerProperty playCount,
                         LocalDateTime lastPlayedDate,
                         StringProperty lastPlayedDisplayDate,
                         int rating,
                         LocalDate ratedDate,
                         StringProperty ratedDisplayDate,
                         String mimeType,
                         int bitRate,
                         int sampleRate,
                         int bitsPerSample,
                         String fileName,
                         String fileLocation,
                         String fileExtension,
                         long fileSize,
                         String fileDisplaySize,
                         String cueFileName,
                         Long parentId,
                         double startTimeInSeconds,
                         LocalDateTime addedToWatchFolderDate,
                         String addedToWatchFolderDisplayDate) {

    public TrackModel(Track track) {
        this(
                track.id(),
                track.title(),
                track.number(),
                track.comment(),
                track.lyrics(),
                track.artistName(),
                track.albumName(),
                track.genre(),
                track.releaseDate(),
                track.length().seconds(),
                track.length().readable(),
                new SimpleIntegerProperty(track.playback().count()),
                track.playback().lastPlayedDate(),
                new SimpleStringProperty(DateUtils.ldtFormatted(track.playback().lastPlayedDate())),
                track.rating().value(),
                track.rating().ratedDate(),
                new SimpleStringProperty(DateUtils.ldFormatted(track.rating().ratedDate())),
                track.audioFormat().mimeType(),
                track.audioFormat().bitRate(),
                track.audioFormat().sampleRate(),
                track.audioFormat().bitsPerSample(),
                track.fileAttributes().name(),
                track.fileAttributes().location(),
                track.fileAttributes().extension(),
                track.fileAttributes().size(),
                track.fileAttributes().readableSize(),
                track.cueInfo().cueFile(),
                track.cueInfo().parentId(),
                track.cueInfo().startTimeInSeconds(),
                track.additionalInfo().addedToWatchFolderDate(),
                DateUtils.ldtFormatted(track.playback().lastPlayedDate())
        );
    }

}
