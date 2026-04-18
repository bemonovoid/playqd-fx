package io.playqd.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.caprica.vlcj.media.MediaRef;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerEventListener;

class MediaListPlayerEventListenerImpl implements MediaListPlayerEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(MediaListPlayerEventListenerImpl.class);

    @Override
    public void mediaListPlayerFinished(MediaListPlayer mediaListPlayer) {
        LOG.info("End of media list. No more items to play.");
    }

    @Override
    public void nextItem(MediaListPlayer mediaListPlayer, MediaRef item) {
        var trackRefs = Player.getPlayerListTrackRefs();
        var media = item.newMedia();
        var mrl = media.info().mrl();
        media.release();
        trackRefs.stream()
                .filter(trackRef -> trackRef.mrl().equals(mrl))
                .findFirst()
                .ifPresent(trackRef -> {
                    LOG.info("Next item: {} ({} - {})",
                            trackRef.mrl(), trackRef.track().artistName(), trackRef.track().name());
                    mediaListPlayer.mediaPlayer().mediaPlayer().userData(trackRef);
                });
    }

    @Override
    public void stopped(MediaListPlayer mediaListPlayer) {
        LOG.info("Media list has stopped.");
    }
}
