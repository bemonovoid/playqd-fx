package io.playqd.dbus;

import io.playqd.player.TrackRef;

public class MprisNotifierDelegate {

    private final MprisNotifier mprisNotifier;

    public MprisNotifierDelegate(MprisNotifier mprisNotifier) {
        this.mprisNotifier = mprisNotifier;
    }

    public void notifyTrackChanged(TrackRef trackRef) {
        try {
            if (trackRef != null) {
                mprisNotifier.updateMetadata(trackRef);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyIsPlaying() {
        mprisNotifier.updatePlaybackStatus("Playing");
    }

    public void notifyIsPaused() {
        mprisNotifier.updatePlaybackStatus("Paused");
    }

    public void notifyIsStopped() {
        mprisNotifier.updatePlaybackStatus("Stopped");
    }

    public void onTimeChanged(long newTime) {
        mprisNotifier.updateSeekTime(newTime);
    }
}
