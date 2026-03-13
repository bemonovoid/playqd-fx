package io.playqd.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;

class MediaPlayerEventAdapterImpl extends MediaPlayerEventAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(MediaPlayerEventAdapterImpl.class);

    private final PlayerQueue playerQueue;

    MediaPlayerEventAdapterImpl(PlayerQueue playerQueue) {
        this.playerQueue = playerQueue;
    }

    @Override
    public void paused(MediaPlayer mediaPlayer) {
        Player.PAUSED_PROPERTY.set(true);
    }

    @Override
    public void playing(MediaPlayer mediaPlayer) {
        Player.PAUSED_PROPERTY.set(false);
        Player.STOPPED_PROPERTY.set(false);

        Player.PLAYING_TRACK_PROPERTY.set(playerQueue.current().orElse(null));
    }

    @Override
    public void stopped(MediaPlayer mediaPlayer) {
        Player.STOPPED_PROPERTY.set(true);
    }

    @Override
    public void finished(MediaPlayer mediaPlayer) {
        Player.FINISHED_PROPERTY.set(playerQueue.current().orElse(null));
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
