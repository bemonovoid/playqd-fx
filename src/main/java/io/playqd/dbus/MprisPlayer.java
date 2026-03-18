package io.playqd.dbus;

import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.types.Variant;

import java.util.Map;

@DBusInterfaceName("org.mpris.MediaPlayer2.Player")
public interface MprisPlayer extends DBusInterface {

    void Next();

    void Previous();

    void Pause();

    void PlayPause();

    void Stop();

    void Play();

    void Seek(long OffsetRelative);

    void SetPosition(DBusPath TrackId, long Position);

    void OpenUri(String Uri);

    // Metadata is the "Magic" property for the toolbar
    Map<String, Variant<?>> Metadata();

    String PlaybackStatus(); // "Playing", "Paused", or "Stopped"

    double Volume();

    long Position();

}
