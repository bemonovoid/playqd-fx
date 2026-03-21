package io.playqd.dbus;

import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.annotations.DBusBoundProperty;
import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.annotations.DBusProperty;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.messages.DBusSignal;

@DBusInterfaceName("org.mpris.MediaPlayer2.Player")
public interface MprisPlayer extends DBusInterface {

    void Next();

    void Previous();

    void Pause();

    void PlayPause();

    void Stop();

    void Play();

    @DBusBoundProperty(access = DBusProperty.Access.WRITE, name = "Seek")
    void seek(long OffsetRelative);

    void SetPosition(DBusPath TrackId, long Position);

    void OpenUri(String Uri);

//    @DBusBoundProperty(access = DBusProperty.Access.READ, name = "Metadata")
//    Map<String, Variant<?>> metadata();

    @DBusBoundProperty(access = DBusProperty.Access.READ, name = "PlaybackStatus")
    String playbackStatus();

    @DBusBoundProperty(access = DBusProperty.Access.READ, name = "Volume")
    double volume();

    @DBusBoundProperty(access = DBusProperty.Access.READ_WRITE, name = "Position")
    long position();

    @DBusBoundProperty(access = DBusProperty.Access.READ, name = "MinimumRate")
    double minimumRate();

    @DBusBoundProperty(access = DBusProperty.Access.READ, name = "MaximumRate")
    double maximumRate();

    @DBusBoundProperty(access = DBusProperty.Access.READ_WRITE, name = "Rate")
    double rate();

    @DBusBoundProperty(access = DBusProperty.Access.READ, name = "CanRaise")
    boolean canRaise();

    @DBusBoundProperty(access = DBusProperty.Access.READ, name = "CanControl")
    boolean canControl();

    @DBusBoundProperty(access = DBusProperty.Access.READ, name = "CanSeek")
    boolean canSeek();

    @DBusBoundProperty(access = DBusProperty.Access.READ, name = "CanPlay")
    boolean canPlay();

    @DBusBoundProperty(access = DBusProperty.Access.READ, name = "CanPause")
    boolean canPause();

    @DBusBoundProperty(access = DBusProperty.Access.READ, name = "CanGoNext")
    boolean canGoNext();

    @DBusBoundProperty(access = DBusProperty.Access.READ, name = "CanGoPrevious")
    boolean canGoPrevious();

    class Seeked extends DBusSignal {

        private final long timeInUs;

        public Seeked(long timeInUs) throws DBusException {
            super(MprisApplication.OBJECT_PATH);
            this.timeInUs = timeInUs;
        }

        public long getTimeInUs() {
            return timeInUs;
        }
    }

}
