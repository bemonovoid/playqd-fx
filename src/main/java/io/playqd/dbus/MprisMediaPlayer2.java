package io.playqd.dbus;

import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.interfaces.DBusInterface;

@DBusInterfaceName("org.mpris.MediaPlayer2")
public interface MprisMediaPlayer2 extends DBusInterface {

    void Raise();

    void Quit();

    // Properties the applet reads
    String Identity(); // Your App Name (e.g., "My vlcj Player")

    String DesktopEntry(); // Your .desktop file name (optional)

    boolean CanQuit();

    boolean CanRaise();

    boolean HasTrackList();

}
