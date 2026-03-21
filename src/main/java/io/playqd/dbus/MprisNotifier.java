package io.playqd.dbus;

import io.playqd.player.TrackRef;
import io.playqd.utils.PlayqdApis;
import org.freedesktop.dbus.Marshalling;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MprisNotifier {

    private static final Logger LOG = LoggerFactory.getLogger(MprisNotifier.class);

    private final DBusConnection dBusConnection;

    public MprisNotifier(DBusConnection dBusConnection) {
        this.dBusConnection = dBusConnection;
    }

    public void updateMetadata(TrackRef trackRef) {
        emitMetadataChanged(buildMetadata(trackRef));
    }

    private void emitMetadataChanged(Map<String, Variant<?>> metadata) {
        var properties = new HashMap<String, Variant<?>>();
        try {
            var variantSignature = Marshalling.convertJavaClassesToSignature(Map.class, String.class, Variant.class);
            var metadataVariant = new Variant<>(metadata, variantSignature);
            properties.put("Metadata", metadataVariant);
        } catch (IllegalArgumentException e) {
            LOG.error("Error initializing 'Metadata' variant.", e);
            return;
        }
        try {
            var signal = new Properties.PropertiesChanged(
                    MprisApplication.OBJECT_PATH,
                    "org.mpris.MediaPlayer2.Player",
                    properties,
                    Collections.emptyList());
            dBusConnection.sendMessage(signal);
        } catch (DBusException e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePlaybackStatus(String status) {
        var properties = new HashMap<String, Variant<?>>();
        properties.put("PlaybackStatus", new Variant<>(status));
        if ("Playing".equals(status)) {
            properties.put("Rate", new Variant<>(1.0));
        } else {
            properties.put("Rate", new Variant<>(0.0));
        }
        try {
            var signal = new Properties.PropertiesChanged(
                    MprisApplication.OBJECT_PATH,
                    "org.mpris.MediaPlayer2.Player",
                    properties,
                    Collections.emptyList());
            dBusConnection.sendMessage(signal);
        } catch (DBusException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateSeekTime(long newTimeInMillis) {
        try {
            var seeked = new MprisPlayer.Seeked(newTimeInMillis * 1000L);
            dBusConnection.sendMessage(seeked);
        } catch (DBusException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Variant<?>> buildMetadata(TrackRef trackRef) {
        var properties = new HashMap<String, Variant<?>>();
        properties.put("mpris:trackid", new Variant<>(MprisApplication.OBJECT_PATH + "/Track/" + trackRef.track().id()));
        properties.put("mpris:length", new Variant<>(trackRef.track().length().seconds() * 1000L * 1000L));
        properties.put("mpris:artUrl", new Variant<>(PlayqdApis.albumArtwork(trackRef.track().id())));
        properties.put("xesam:title", new Variant<>(trackRef.track().title()));
        properties.put("xesam:artist", new Variant<>(trackRef.track().artistName()));
        return properties;
    }
}
