package io.playqd.player;

import io.playqd.Application;
import io.playqd.config.AppConfig;
import io.playqd.dbus.DBusConnectionInfo;
import io.playqd.dbus.MPRIS;
import io.playqd.dbus.MPRISBuilder;
import io.playqd.dbus.VLCEventToMPRISAdapterImpl;
import org.freedesktop.dbus.exceptions.DBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;

import java.io.IOException;
import java.util.Optional;

public class MprisApplication {

    private static final Logger LOG = LoggerFactory.getLogger(MprisApplication.class);

    public static final String APP_BUS_NAME = "org.mpris.MediaPlayer2.PlayqdPlayerApp";
    public static final String OBJECT_PATH = "/org/mpris/MediaPlayer2";

    static final String APPLICATION_NAME = "PlayqdFx Player";

    private static MprisApplication INSTANCE;

    private MPRIS mpris;
    private MediaPlayerEventAdapter vlcEventToMPRISAdapter;

    private final MediaPlayer mediaPlayer;
    private final MediaListPlayer mediaListPlayer;

    public static MprisApplication getInstance() {
        return INSTANCE;
    }

    static MprisApplication init(MediaPlayer mediaPlayer, MediaListPlayer mediaListPlayer) {
        if (INSTANCE == null) {
            INSTANCE = new MprisApplication(mediaPlayer, mediaListPlayer);
        }
        return INSTANCE;
    }

    private MprisApplication(MediaPlayer mediaPlayer, MediaListPlayer mediaListPlayer) {
        this.mediaPlayer = mediaPlayer;
        this.mediaListPlayer = mediaListPlayer;
    }

    public boolean isEnabled() {
        return AppConfig.getProperties().player().dbus().enabled().get();
    }

    public void start() {
        if (!isEnabled()) {
            LOG.warn("Not started, DBUS is disabled: {}", "player.dbus.enabled=false");
            return;
        }
        if (mpris != null) {
            if (mpris.isConnected()) {
                LOG.warn("Already started.");
                return;
            } else {
                removeMediaPlayerEventListener();
            }
        }
        try {
            mpris = new MPRISBuilder()
                    .setIdentity(APPLICATION_NAME)
                    .setDesktopEntry(APPLICATION_NAME)
                    .setCanQuit(true)
                    .setCanPlay(true)
                    .setCanControl(true)
                    .setCanRaise(true)
                    .setCanSeek(true)
                    .setCanPlay(true)
                    .setCanPause(true)
                    .setCanGoNext(true)
                    .setCanGoPrevious(true)
                    .setOnQuit(Application::exit)
                    .setOnPlay(() -> mediaListPlayer.controls().play())
                    .setOnPause(() -> mediaListPlayer.controls().pause())
                    .setOnStop(() -> mediaListPlayer.controls().stop())
                    .setOnNext(() -> mediaListPlayer.controls().playNext())
                    .setOnPrevious(() -> mediaListPlayer.controls().playPrevious())
                    .setOnPlayPause(() -> {
                        if (mediaPlayer.status().isPlaying()) {
                            mediaPlayer.controls().pause();
                        } else {
                            mediaListPlayer.controls().play();
                        }
                    })
                    .setOnSeek(value -> mediaPlayer.controls().skipTime(value / 1000))
                    .setOnSetPosition(value -> mediaPlayer.controls().setTime(value.getPosition() / 1000))
                    .setOnVolume(value -> mediaPlayer.audio().setVolume(value.intValue()))
                    .setRate(1)
                    .setMinimumRate(1)
                    .setMaximumRate(1)
                    .build(APP_BUS_NAME);

            //TODO connection.releasename(APP_BUS_NAME)

            removeMediaPlayerEventListener();
            mediaPlayer.events().addMediaPlayerEventListener(
                    vlcEventToMPRISAdapter = new VLCEventToMPRISAdapterImpl(mpris));

            LOG.info("Added 'vlcEventToMPRISAdapter' to media player event listeners.");
            LOG.info("Mpris application started. DBUS connected: {}", mpris.isConnected());
        } catch (DBusException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        if (mpris.isConnected()) {
            try {
                mpris.getConnection().close();
            } catch (IOException e) {
                LOG.error("Error occurred when closing DbusConnection. {}", e.getMessage(), e);
            }
        }
        mpris = null;
        removeMediaPlayerEventListener();
    }

    public Optional<DBusConnectionInfo> getInfo() {
        if (isEnabled()) {
            return Optional.of(new DBusConnectionInfo(
                    mpris.getConnection().isConnected(),
                    APPLICATION_NAME,
                    mpris.getConnection().getMachineId(),
                    mpris.getConnection().getNames(),
                    mpris.getConnection().getAddress()));
        }
        return Optional.empty();
    }

    private void removeMediaPlayerEventListener() {
        if (vlcEventToMPRISAdapter != null) {
            mediaPlayer.events().removeMediaPlayerEventListener(vlcEventToMPRISAdapter);
            vlcEventToMPRISAdapter = null;
            LOG.info("Removed 'vlcEventToMPRISAdapter' from media player event listeners.");
        }
    }

}
