package io.playqd.dbus;

import io.playqd.client.PlayqdApis;
import io.playqd.player.MprisApplication;
import io.playqd.player.TrackRef;
import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.exceptions.DBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;

import java.net.URI;
import java.util.List;

public class VLCEventToMPRISAdapterImpl extends MediaPlayerEventAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(VLCEventToMPRISAdapterImpl.class);

    private final MPRIS mpris;

    public VLCEventToMPRISAdapterImpl(MPRIS mpris) {
        this.mpris = mpris;
    }

    @Override
    public void mediaPlayerReady(MediaPlayer mediaPlayer) {
        var trackRef = (TrackRef) mediaPlayer.userData();
        if (trackRef == null) {
            LOG.warn("Can't update metadata. Retrieved track ref is null.");
            return;
        }
        try {
            var metadata = new Metadata.Builder()
                    .setTrackID(new DBusPath(MprisApplication.OBJECT_PATH + "/Track/" + trackRef.track().id()))
                    .setLength((int) (trackRef.track().length().seconds() * 1000L * 1000L))
                    .setArtURL(URI.create(PlayqdApis.trackArtwork(trackRef.track().id())))
                    .setTitle(trackRef.track().title())
                    .setArtists(List.of(trackRef.track().artistName()))
                    .build();
            mpris.setMetadata(metadata);
        } catch (DBusException e) {
            LOG.error("Metadata update failed. {}", e.getMessage(), e);
        }
    }

    @Override
    public void playing(MediaPlayer mediaPlayer) {
        try {
            mpris.setPlaybackStatus(PlaybackStatus.PLAYING);
        } catch (DBusException e) {
            LOG.error("Playback status update failed. {}", e.getMessage(), e);
        }
    }

    @Override
    public void paused(MediaPlayer mediaPlayer) {
        try {
            mpris.setPlaybackStatus(PlaybackStatus.PAUSED);
        } catch (DBusException e) {
            LOG.error("Playback status update failed. {}", e.getMessage(), e);
        }
    }

    @Override
    public void stopped(MediaPlayer mediaPlayer) {
        try {
            mpris.setPlaybackStatus(PlaybackStatus.STOPPED);
        } catch (DBusException e) {
            LOG.error("Playback status update failed. {}", e.getMessage(), e);
        }
    }

    @Override
    public void volumeChanged(MediaPlayer mediaPlayer, float volume) {
        try {
            mpris.setVolume(volume);
        } catch (DBusException e) {
            LOG.error("Setting volume failed. {}", e.getMessage(), e);
        }
    }

}
