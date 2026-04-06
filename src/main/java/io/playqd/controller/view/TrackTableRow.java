package io.playqd.controller.view;

import io.playqd.data.Track;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDateTime;

public final class TrackTableRow {

    private final Track track;
    private final IntegerProperty playCount;
    private final ObjectProperty<LocalDateTime> lastPlayedDate;
    private final IntegerProperty rating;
    private final ObjectProperty<LocalDateTime> ratedDate;

    public TrackTableRow(Track track) {
        this.track = track;
        this.playCount = new SimpleIntegerProperty(track.playback().count());
        this.lastPlayedDate = new SimpleObjectProperty<>(track.playback().lastPlayedDate());
        this.rating = new SimpleIntegerProperty(track.rating().value());
        this.ratedDate = new SimpleObjectProperty<>(track.rating().ratedDate());
    }

    public Track track() {
        return track;
    }

    public long getArtworkTrackId() {
        return track.id();
    }

    public String getTitle() {
        return track().title();
    }

    public Integer getTime() {
        return track.length().seconds();
    }

    public String getNumber() {
        return track.number();
    }

    public String getArtist() {
        return track.artistName();
    }

    public String getAlbum() {
        return track.albumName();
    }

    public String getGenre() {
        return track.genre();
    }

    public String getFileName() {
        return track.fileAttributes().name();
    }

    public String getExtension() {
        return track.fileAttributes().extension();
    }

    public String getMimeType() {
        return track.audioFormat().mimeType();
    }

    public Long getSize() {
        return track.fileAttributes().size();
    }

    public Integer getBitsPerSample() {
        return track.audioFormat().bitsPerSample();
    }

    public int getBitRate() {
        return track.audioFormat().bitRate();
    }

    public  int getSampleRate() {
        return track.audioFormat().sampleRate();
    }

    public LocalDateTime getAddedDate() {
        return track.fileAttributes().createdDate();
    }

    public IntegerProperty getPlayCount() {
        return playCount;
    }

    public ObjectProperty<LocalDateTime> getLastPlayedDate() {
        return lastPlayedDate;
    }

    public IntegerProperty getRating() {
        return rating;
    }

    public ObjectProperty<LocalDateTime> getRatedDate() {
        return ratedDate;
    }

    public void setObservableProperties(Track track) {
        if (track().rating().value() != track.rating().value()) {
            rating.set(track.rating().value());
            ratedDate.set(track.rating().ratedDate());
        }
        if (track.playback().count() != playCount.get()) {
            playCount.set(track.playback().count());
            lastPlayedDate.set(track.playback().lastPlayedDate());
        }
    }

}
