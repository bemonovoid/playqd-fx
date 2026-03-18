//package io.playqd.dbus;
//
//import org.freedesktop.dbus.DBusPath;
//import org.freedesktop.dbus.connections.impl.DBusConnection;
//import org.freedesktop.dbus.exceptions.DBusException;
//import org.freedesktop.dbus.interfaces.Properties;
//import org.freedesktop.dbus.types.Variant;
//import uk.co.caprica.vlcj.player.base.MediaPlayer;
//import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//
//public class MprisPlayerImpl implements MprisPlayer, MprisMediaPlayer2 {
//
//    private final DBusConnection connection;
//
//    public MprisPlayerImpl(DBusConnection connection) {
//        this.connection = connection;
//    }
//
//    @Override
//    public void Raise() {
//
//    }
//
//    @Override
//    public void Quit() {
//        System.exit(0);
//    }
//
//    @Override
//    public String Identity() {
//        return "Playqd Media Player";
//    }
//
//    @Override
//    public String DesktopEntry() {
//        return "vlcj-app";
//    }
//
//    @Override
//    public boolean CanQuit() {
//        return true;
//    }
//
//    @Override
//    public boolean CanRaise() {
//        return false;
//    }
//
//    @Override
//    public boolean HasTrackList() {
//        return false;
//    }
//
//    @Override
//    public void Next() {
//        vlcPlayer.controls().skipForward();
//    }
//
//    @Override
//    public void Previous() {
//        vlcPlayer.controls().skipBackward();
//    }
//
//    @Override
//    public void Pause() {
//        vlcPlayer.controls().pause();
//    }
//
//    @Override
//    public void PlayPause() {
//        vlcPlayer.controls().pause();
//    }
//
//    @Override
//    public void Stop() {
//        vlcPlayer.controls().stop();
//    }
//
//    @Override
//    public void Play() {
//        vlcPlayer.controls().play();
//    }
//
//    @Override
//    public void Seek(long OffsetRelative) {
//        vlcPlayer.controls().skipTime(offset / 1000);
//    }
//
//    @Override
//    public void SetPosition(DBusPath TrackId, long Position) {
//        vlcPlayer.controls().setTime(pos / 1000);
//    }
//
//    @Override
//    public void OpenUri(String Uri) {
//        vlcPlayer.media().play(uri);
//    }
//
//    @Override
//    public Map<String, Variant<?>> Metadata() {
//        var meta = new HashMap<String, Variant<?>>();
//        // vlcj uses milliseconds; MPRIS expects microseconds
//        long lengthMicros = vlcPlayer.media().info().duration() * 1000;
//
//        meta.put("mpris:trackid", new Variant<>(new DBusPath("/org/mpris/MediaPlayer2/CurrentTrack")));
//        meta.put("mpris:length", new Variant<>(lengthMicros));
//        meta.put("xesam:title", new Variant<>(vlcPlayer.media().meta().title()));
//        meta.put("xesam:artist", new Variant<>(Collections.singletonList(vlcPlayer.media().meta().artist())));
//        // Optional: meta.put("mpris:artUrl", new Variant<>("file:///path/to/cover.png")); //TODO
//
//        return meta;
//    }
//
//    @Override
//    public String PlaybackStatus() {
//        if (vlcPlayer.status().isPlaying()) {
//            return "Playing";
//        } else if (vlcPlayer.status().isPauseable()) {
//            return "Paused";
//        } else {
//            return "Stopped";
//        }
//    }
//
//    @Override
//    public double Volume() {
//        return vlcPlayer.audio().volume() / 100.0;
//    }
//
//    @Override
//    public long Position() {
//        return vlcPlayer.status().time() * 1000;
//    }
//
//    @Override
//    public String getObjectPath() {
//        return "/org/mpris/MediaPlayer2";
//    }
//
//    private void setupVlcjListeners() {
//        vlcPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
//            @Override
//            public void playing(MediaPlayer mediaPlayer) {
//                signalPropertyChange("PlaybackStatus", "Playing");
//            }
//
//            @Override
//            public void paused(MediaPlayer mediaPlayer) {
//                signalPropertyChange("PlaybackStatus", "Paused");
//            }
//
//            @Override
//            public void mediaChanged(MediaPlayer mediaPlayer, uk.co.caprica.vlcj.media.MediaRef media) {
//                signalPropertyChange("Metadata", Metadata());
//            }
//        });
//    }
//
//    private void signalPropertyChange(String propertyName, Object value) {
//        try {
//            Map<String, Variant<?>> changedProps = new HashMap<>();
//            changedProps.put(propertyName, new Variant<>(value));
//
//            // MPRIS properties belong to the "Player" interface
//            Properties.PropertiesChanged signal = new Properties.PropertiesChanged(
//                    "/org/mpris/MediaPlayer2",
//                    "org.mpris.MediaPlayer2.Player",
//                    changedProps,
//                    Collections.emptyList()
//            );
//
//            connection.sendMessage(signal);
//        } catch (DBusException e) {
//            e.printStackTrace();
//        }
//    }
//}
