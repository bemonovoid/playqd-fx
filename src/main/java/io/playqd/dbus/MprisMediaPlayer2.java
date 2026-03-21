package io.playqd.dbus;

import org.freedesktop.dbus.annotations.DBusBoundProperty;
import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.annotations.DBusProperty;
import org.freedesktop.dbus.interfaces.DBusInterface;

@DBusInterfaceName("org.mpris.MediaPlayer2")
public interface MprisMediaPlayer2 extends DBusInterface {

    /**
     * Can't use DBusBoundProperty, because 'Raise' method has neither an argument nor returns any value.
     */
    void Raise();

    /**
     * Can't use DBusBoundProperty, because 'Quit' method has neither an argument nor returns any value.
     */
    void Quit();

    @DBusBoundProperty(access = DBusProperty.Access.READ, name = "Identity")
    String identity();

    @DBusBoundProperty(access = DBusProperty.Access.READ, name = "DesktopEntry")
    String desktopEntry(); // Your .desktop file name (optional)

    @DBusBoundProperty(access = DBusProperty.Access.READ, name = "CanQuit")
    boolean canQuit();

    @DBusBoundProperty(access = DBusProperty.Access.READ, name = "HasTrackList")
    boolean hasTrackList();

}
