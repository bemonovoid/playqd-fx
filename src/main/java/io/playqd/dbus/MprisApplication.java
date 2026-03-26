package io.playqd.dbus;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder;
import org.freedesktop.dbus.exceptions.DBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;

import java.io.IOException;

public final class MprisApplication {

    private static final Logger LOG = LoggerFactory.getLogger(MprisApplication.class);

    public static final String OBJECT_PATH = "/org/mpris/MediaPlayer2";
    public static final String APP_BUS_NAME = "org.mpris.MediaPlayer2.PlayqdPlayerApp";

    static final String APPLICATION_IDENTITY = "PlayqdFx Player";

    private static final DBusConnection CONNECTION;

    private static final MprisApplication MPRIS_APPLICATION = new MprisApplication();
    private static final MprisNotifierDelegate MPRIS_NOTIFIER_DELEGATE;

    static {
        try {
            CONNECTION = DBusConnectionBuilder.forSessionBus().build();
            CONNECTION.requestBusName(APP_BUS_NAME);
            MPRIS_NOTIFIER_DELEGATE = new MprisNotifierDelegate(new MprisNotifier(CONNECTION));
        } catch (DBusException e) {
            throw new RuntimeException(e);
        }
    }

    public static MprisApplication getInstance() {
        return MPRIS_APPLICATION;
    }

    public void start(MediaPlayer mediaPlayer, MediaListPlayer mediaListPlayer) {
        try {
            var mprisPlayer = new MprisPlayerImpl(mediaPlayer, mediaListPlayer);
            CONNECTION.exportObject(OBJECT_PATH, mprisPlayer);
        } catch (DBusException e) {
            throw new RuntimeException(e);
        }
    }

    public MprisNotifierDelegate getMprisNotifier() {
        return MPRIS_NOTIFIER_DELEGATE;
    }

    public void close() {
        try {
            if (CONNECTION.isConnected()) {
                LOG.info("Closing DBusConnection ...");
                CONNECTION.close();
                LOG.info("DBusConnection was successfully closed.");
            }
        } catch (IOException e) {
            LOG.error("Failed to gracefully close DBusConnection.", e);
        }
    }

    public DBusConnectionInfo getInfo() {
        return new DBusConnectionInfo(
                CONNECTION.isConnected(),
                APPLICATION_IDENTITY,
                CONNECTION.getMachineId(),
                CONNECTION.getNames(),
                CONNECTION.getAddress());
    }

    private MprisApplication() {

    }
}
