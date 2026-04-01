package io.playqd.player;

import io.playqd.data.Track;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerEventListener;
import uk.co.caprica.vlcj.player.list.PlaybackMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class Player {

    private static final Logger LOG = LoggerFactory.getLogger(Player.class);

    private static final MediaPlayer MEDIA_PLAYER;
    private static final MediaPlayerFactory MEDIA_PLAYER_FACTORY;

    static final SimpleObjectProperty<Track> FINISHED_PROPERTY = new SimpleObjectProperty<>();
    static final SimpleObjectProperty<Track> PLAYING_TRACK_PROPERTY = new SimpleObjectProperty<>();
    static final SimpleObjectProperty<Float> POSITION_CHANGED_PROPERTY = new SimpleObjectProperty<>();
    static final SimpleBooleanProperty PAUSED_PROPERTY = new SimpleBooleanProperty();
    static final SimpleBooleanProperty STOPPED_PROPERTY = new SimpleBooleanProperty();
    static final SimpleObjectProperty<Long> TIME_CHANGED_PROPERTY = new SimpleObjectProperty<>();

    static MediaListPlayerEventListener LIST_PLAYER_EVENT_LISTENER;

    private static final MediaListPlayer MEDIA_LIST_PLAYER;

    static {
        MEDIA_PLAYER_FACTORY = new MediaPlayerFactory();
        MEDIA_PLAYER = MEDIA_PLAYER_FACTORY.mediaPlayers().newMediaPlayer();
        MEDIA_LIST_PLAYER = newMediaListPlayerInstance();
        MprisApplication.init(MEDIA_PLAYER, MEDIA_LIST_PLAYER).start();
        var mediaPlayerEventListener = new MediaPlayerEventAdapterImpl(MEDIA_LIST_PLAYER);
        MEDIA_PLAYER.events().addMediaPlayerEventListener(mediaPlayerEventListener);
        addLoggingListeners();
    }

    private static MediaListPlayer newMediaListPlayerInstance() {
        var instance = MEDIA_PLAYER_FACTORY.mediaPlayers().newMediaListPlayer();
        instance.mediaPlayer().setMediaPlayer(MEDIA_PLAYER);
        instance.controls().setMode(PlaybackMode.DEFAULT);
        return instance;
    }

    public static Optional<Track> playingTrack() {
        return Optional.ofNullable((TrackRef) MEDIA_PLAYER.userData()).map(TrackRef::track);
    }

    static void enqueueAndPlay(TrackListRequest trackListRequest) {
        var playTrackRef = enqueueNewList(trackListRequest);
        play(trackListRequest.firstTrackPosition(), playTrackRef);
    }

    static void enqueue(TrackListRequest trackListRequest) {
        var trackRefs = trackRefs(trackListRequest.tracks());
        if (MEDIA_LIST_PLAYER.list().media() == null) {
            enqueueNewList(trackListRequest);
        } else {
            var userDataRefs = getPlayerListTrackRefs();
            userDataRefs.addAll(trackListRequest.firstTrackPosition(), trackRefs); // Update current list snapshot
            for (int i = 0; i < trackRefs.size(); i++) { // update player list itself
                var trackRef = trackRefs.get(i);
                var insertAt = trackListRequest.firstTrackPosition() + i;
                MEDIA_LIST_PLAYER.list().media().insert(insertAt, trackRef.mrl(), trackRef.options());
            }
        }
    }

    private static TrackRef enqueueNewList(TrackListRequest trackListRequest) {
        var index = trackListRequest.firstTrackPosition();

        var trackRefs = trackRefs(trackListRequest.tracks());

        MEDIA_LIST_PLAYER.userData(new ArrayList<>(trackRefs));

        var playTrackRef = trackRefs.get(index);

        LOG.info("Created new media list with {} tracks. Playback starts at index: {} ({} - {})",
                trackRefs.size(), index, playTrackRef.track().artistName(), playTrackRef.track().title());

        setNewMediaList(trackRefs);

        return playTrackRef;
    }

    static void remove(List<Integer> indices) {
        if (MEDIA_LIST_PLAYER.list().media() == null) {
            return;
        }
        if (indices == null || indices.isEmpty()) {
            return;
        }
        var userDataTrackRefs = getPlayerListTrackRefs();
        indices.forEach(idx -> {
            userDataTrackRefs.remove(idx.intValue());
            MEDIA_LIST_PLAYER.list().media().remove(idx);
        });
    }

    static void clearMediaList() {
        if (MEDIA_LIST_PLAYER.list().media() == null) {
            return;
        }
        getPlayerListTrackRefs().clear();
        MEDIA_LIST_PLAYER.list().media().clear();
    }

    public static void play(Track track) {
        LOG.info("Requesting track from media list: {} - {}.", track.artistName(), track.title());
        if (MEDIA_LIST_PLAYER.list().media() != null) {
            var trackRef = new TrackRef(track);
            var listSize = MEDIA_LIST_PLAYER.list().media().count();
            for (int idx = 0; idx < listSize; idx++) {
                var ref = MEDIA_LIST_PLAYER.list().media().newMediaRef(idx);
                if (ref != null) {
                    var mrl = ref.newMedia().info().mrl();
                    if (trackRef.mrl().equals(mrl)) {
                        LOG.info("Track was found at index {}", idx);
                        play(idx, trackRef);
                        return;
                    }
                }
            }
        }
        LOG.info("Track was not found.");
    }

    private static void play(int index, TrackRef trackRef) {
        // Need to reset the listener when playing by index, otherwise the list listener triggers on previous mrl
        // preventing to grab current mrl correctly.
        // The listener is reset upon playing callback in media player listener.
        if (LIST_PLAYER_EVENT_LISTENER != null) {
            MEDIA_LIST_PLAYER.events().removeMediaListPlayerEventListener(LIST_PLAYER_EVENT_LISTENER);
            LIST_PLAYER_EVENT_LISTENER = null;
        }
        MEDIA_LIST_PLAYER.mediaPlayer().mediaPlayer().userData(trackRef);
        MEDIA_LIST_PLAYER.controls().play(index);
    }

    public static boolean playNext() {
        LOG.info("Play next requested.");
        return MEDIA_LIST_PLAYER.controls().playNext();
    }

    public static boolean playPrevious() {
        LOG.info("Play previous requested.");
        return MEDIA_LIST_PLAYER.controls().playPrevious();
    }

    public static void seek(float position) {
        LOG.info("Seeking position from '{}' to '{}'", MEDIA_PLAYER.status().position(), position);
        MEDIA_PLAYER.controls().setPosition(position);
    }

    public static void pause() {
        if (isPlaying()) {
            MEDIA_PLAYER.controls().setPause(true);
        }
    }

    public static void stop() {
        MEDIA_LIST_PLAYER.controls().stop();
    }

    public static void resume() {
        if (!isPlaying()) {
            MEDIA_PLAYER.controls().setPause(false);
        }
        if (!isPlaying()) {
            MEDIA_PLAYER.controls().play();
        }
    }

    public static boolean isPlaying() {
        return MEDIA_PLAYER.status().isPlaying();
    }

    public static void setVolume(int volume) {
        MEDIA_PLAYER.audio().setVolume(volume);
    }

    public static int getVolume() {
        return MEDIA_PLAYER.audio().volume();
    }

    public static void fetchMode(FetchMode mode) {
        switch (mode) {
            case NORMAL -> setNewMediaList(getPlayerListTrackRefs());
            case RANDOM -> {
                var shuffledTrackRefs = getPlayerListTrackRefs();
                Collections.shuffle(shuffledTrackRefs);
                setNewMediaList(shuffledTrackRefs);
            }
        }
        LOG.info("Media list player fetch mode was set to {}", mode);
    }

    public static void loopMode(LoopMode mode) {
        var playbackMode = switch (mode) {
            case ON -> PlaybackMode.LOOP;
            case OFF -> PlaybackMode.DEFAULT;
            case SINGLE -> PlaybackMode.REPEAT;
        };
        MEDIA_LIST_PLAYER.controls().setMode(playbackMode);
        LOG.info("Media list player playback mode was set to {}", mode);
    }

    public static void onPaused(Consumer<Boolean> callback) {
        PAUSED_PROPERTY.addListener((_, _, paused) -> callback.accept(paused != null && paused));
    }

    public static void onStopped(Consumer<Boolean> callback) {
        STOPPED_PROPERTY.addListener((_, _, stopped) -> callback.accept(stopped != null && stopped));
    }

    public static void onFinished(Consumer<Track> callback) {
        FINISHED_PROPERTY.addListener((_, _, finishedTrack) -> callback.accept(finishedTrack));
    }

    public static void onPositionChanged(Consumer<Float> callback) {
        POSITION_CHANGED_PROPERTY.addListener((_, _, newPosition) -> callback.accept(newPosition));
    }

    public static void onTimeChanged(Consumer<Long> callback) {
        TIME_CHANGED_PROPERTY.addListener((_, _, newTime) -> {
            callback.accept(newTime);
        });
    }

    public static void onPlayingTrackChanged(Consumer<Track> callback) {
        onPlayingTrackChanged(callback, null);
    }

    public static void onPlayingTrackChanged(Consumer<Track> callback, Runnable defaultIfEmpty) {
        PLAYING_TRACK_PROPERTY.addListener((_, _, playingTrack) -> {
            if (playingTrack == null && defaultIfEmpty != null) {
                defaultIfEmpty.run();
            } else {
                callback.accept(playingTrack);
            }
        });
    }

    /**
     *
     * @return mutable list
     */
    @SuppressWarnings("unchecked")
    static List<TrackRef> getPlayerListTrackRefs() {
        return (List<TrackRef>) MEDIA_LIST_PLAYER.userData();
    }

    private static void setNewMediaList(List<TrackRef> trackRefs) {
        if (MEDIA_LIST_PLAYER.list().media() == null) {
            var mediaListRef = MEDIA_PLAYER_FACTORY.media().newMediaListRef();
            MEDIA_LIST_PLAYER.list().setMediaList(mediaListRef); // old mediaListRef releases inside this setter
        } else {
            MEDIA_LIST_PLAYER.list().media().clear();
        }
        trackRefs.forEach(trackRef -> MEDIA_LIST_PLAYER.list().media().add(trackRef.mrl(), trackRef.options()));
    }

    private static List<TrackRef> trackRefs(List<Track> tracks) {
        return tracks.stream().map(TrackRef::new).toList();
    }

    private static void addLoggingListeners() {
        onPaused(paused -> LOG.info("{}", paused ? "PAUSED" : "RESUMED"));
        onFinished(finishedTrack ->
                LOG.info("FINISHED PLAYING '{} - {}'", finishedTrack.artistName(), finishedTrack.title()));
        onStopped(stopped -> {
            if (stopped) {
                LOG.info("STOPPED.");
            }
        });
        onPlayingTrackChanged(track -> {
            if (track.isCueTrack()) {
                LOG.info("PLAYING (cue track) '{} - {}'", track.artistName(), track.title());
            } else {
                LOG.info("PLAYING '{} - {}'", track.artistName(), track.title());
            }
        });
    }

    public static void close() {
        LOG.info("Closing Player ...");
        if (isPlaying()) {
            stop();
        }
        MEDIA_LIST_PLAYER.release();
        MEDIA_PLAYER.release();
        LOG.info("Player was closed.");
    }

}
