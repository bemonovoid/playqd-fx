package io.playqd.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;

class MediaPlayerEventAdapterImpl extends MediaPlayerEventAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(MediaPlayerEventAdapterImpl.class);

    private final MediaListPlayer mediaListPlayer;
    private final PlayerObservableProperties playerProperties;

    MediaPlayerEventAdapterImpl(MediaListPlayer mediaListPlayer, PlayerObservableProperties playerProperties) {
        this.mediaListPlayer = mediaListPlayer;
        this.playerProperties = playerProperties;
    }

    @Override
    public void mediaPlayerReady(MediaPlayer mediaPlayer) {
        playerProperties.setPausedProperty(false);
        playerProperties.setStoppedProperty(false);

        if (Player.LIST_PLAYER_EVENT_LISTENER == null) {
            Player.LIST_PLAYER_EVENT_LISTENER = new MediaListPlayerEventListenerImpl();
            mediaListPlayer.events().addMediaListPlayerEventListener(Player.LIST_PLAYER_EVENT_LISTENER);
        }

        var trackRef = (TrackRef) mediaPlayer.userData();
        if (trackRef != null) {
            playerProperties.setPlayingTrackProperty(trackRef.track());
        }
    }

    @Override
    public void playing(MediaPlayer mediaPlayer) {
        playerProperties.setPausedProperty(false);
        playerProperties.setStoppedProperty(false);
    }

    @Override
    public void paused(MediaPlayer mediaPlayer) {
        playerProperties.setPausedProperty(true);
    }

    @Override
    public void stopped(MediaPlayer mediaPlayer) {
        playerProperties.setStoppedProperty(true);

    }

    @Override
    public void finished(MediaPlayer mediaPlayer) {
        var trackRef = (TrackRef) mediaPlayer.userData();
        if (trackRef != null) {
            playerProperties.setFinishedProperty(trackRef.track());
        }
    }

    @Override
    public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
        playerProperties.setPositionChangedProperty(newPosition);
    }

    @Override
    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
        playerProperties.setTimeChangedProperty(newTime);
    }

    @Override
    public void volumeChanged(MediaPlayer mediaPlayer, float volume) {

    }
}
