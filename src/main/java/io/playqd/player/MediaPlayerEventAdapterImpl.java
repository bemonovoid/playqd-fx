package io.playqd.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;

class MediaPlayerEventAdapterImpl extends MediaPlayerEventAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(MediaPlayerEventAdapterImpl.class);

    private final MediaListPlayer mediaListPlayer;

    MediaPlayerEventAdapterImpl(MediaListPlayer mediaListPlayer) {
        this.mediaListPlayer = mediaListPlayer;
    }

    @Override
    public void mediaPlayerReady(MediaPlayer mediaPlayer) {
        Player.PAUSED_PROPERTY.set(false);
        Player.STOPPED_PROPERTY.set(false);

        if (Player.LIST_PLAYER_EVENT_LISTENER == null) {
            Player.LIST_PLAYER_EVENT_LISTENER = new MediaListPlayerEventListenerImpl();
            mediaListPlayer.events().addMediaListPlayerEventListener(Player.LIST_PLAYER_EVENT_LISTENER);
        }

        var trackRef = (TrackRef) mediaPlayer.userData();
        if (trackRef != null) {
            Player.PLAYING_TRACK_PROPERTY.set(trackRef.track());
        }
    }

    @Override
    public void playing(MediaPlayer mediaPlayer) {
        Player.PAUSED_PROPERTY.set(false);
        Player.STOPPED_PROPERTY.set(false);
    }

    @Override
    public void paused(MediaPlayer mediaPlayer) {
        Player.PAUSED_PROPERTY.set(true);
    }

    @Override
    public void stopped(MediaPlayer mediaPlayer) {
        Player.STOPPED_PROPERTY.set(true);
    }

    @Override
    public void finished(MediaPlayer mediaPlayer) {
        var trackRef = (TrackRef) mediaPlayer.userData();
        if (trackRef != null) {
            Player.FINISHED_PROPERTY.set(trackRef.track());
        }
    }

    @Override
    public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
        Player.POSITION_CHANGED_PROPERTY.set(newPosition);
    }

    @Override
    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
        Player.TIME_CHANGED_PROPERTY.set(newTime);
    }

}
