package io.playqd.dbus;

import io.playqd.Application;
import javafx.application.Platform;
import org.freedesktop.dbus.DBusPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;

public class MprisPlayerImpl implements MprisMediaPlayer2, MprisPlayer {

    private static final Logger LOG = LoggerFactory.getLogger(MprisPlayerImpl.class);

    private final MediaPlayer mediaPlayer;
    private final MediaListPlayer mediaListPlayer;

    public MprisPlayerImpl(MediaPlayer mediaPlayer,
                           MediaListPlayer mediaListPlayer) {
        this.mediaPlayer = mediaPlayer;
        this.mediaListPlayer = mediaListPlayer;
    }

    @Override
    public void Raise() {
        LOG.info("Received 'Raise'"); //TODO if I am minimized bring me to the front
    }

    @Override
    public void Quit() {
        Application.exit();
    }

    @Override
    public String identity() {
        LOG.info("Received 'GetIdentity'.");
        return "PlayqdFx Player";
    }

    @Override
    public String desktopEntry() {
        return "playqd-app";
    }

    @Override
    public boolean canQuit() {
        return true;
    }

    @Override
    public boolean canRaise() {
        return true;
    }

    @Override
    public boolean canControl() {
        return true;
    }

    @Override
    public boolean canSeek() {
        return true;
    }

    @Override
    public boolean canPlay() {
        return true;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canGoNext() {
        return true;
    }

    @Override
    public boolean canGoPrevious() {
        return true;
    }

    @Override
    public double minimumRate() {
        return 1.0;
    }

    @Override
    public double maximumRate() {
        return 1.0;
    }

    @Override
    public double rate() {
        return 1.0;
    }

    @Override
    public boolean hasTrackList() {
        return false;
    }

    @Override
    public void Next() {
        LOG.info("Received 'Next'.");
        mediaListPlayer.controls().playNext();
    }

    @Override
    public void Previous() {
        LOG.info("Received 'Previous'.");
        mediaListPlayer.controls().playPrevious();
    }

    @Override
    public void Pause() {
        LOG.info("Received 'Pause'.");
        mediaListPlayer.controls().pause();
    }

    @Override
    public void PlayPause() {
        LOG.info("Received 'PlayPause'.");
        if (mediaPlayer.status().isPlaying()) {
            mediaPlayer.controls().pause();
        } else {
            mediaListPlayer.controls().play();
        }
    }

    @Override
    public void Stop() {
        LOG.info("Received 'Stop'.");
        mediaListPlayer.controls().stop();
    }

    @Override
    public void Play() {
        LOG.info("Received 'Play'.");
        mediaListPlayer.controls().play();
    }

    @Override
    public void seek(long OffsetRelative) {
        LOG.info("Received 'Seek(" + OffsetRelative + ")'.");
        mediaPlayer.controls().skipTime(OffsetRelative / 1000);
    }

    @Override
    public void SetPosition(DBusPath TrackId, long Position) {
        if (!canSeek()) {
            LOG.error("Can't set position the 'CanSeek' is false");
        } else {
            LOG.info("Received 'SetPosition(" + TrackId.getPath() + ", " + Position + ")'.");
            mediaPlayer.controls().setTime(Position / 1000);
        }
    }

    @Override
    public void OpenUri(String Uri) {
        LOG.info("Received 'OpenUri(" + Uri + ")'.");
//        vlcPlayer.media().play(uri);
    }

    @Override
    public String playbackStatus() {
        if (mediaPlayer.status().isPlaying()) {
            return "Playing";
        } else if (mediaPlayer.status().canPause()) {
            return "Paused";
        } else {
            return "Stopped";
        }
    }

    @Override
    public double volume() {
        LOG.info("Received 'Volume'");
        return mediaPlayer.audio().volume() / 100.0;
    }

    @Override
    public long position() {
        var pos = mediaPlayer.status().time() * 1000;
        LOG.info("Received 'Position'. Returns {}", pos);
        return pos;
    }

    @Override
    public String getObjectPath() {
        return MprisApplication.OBJECT_PATH;
    }

}
