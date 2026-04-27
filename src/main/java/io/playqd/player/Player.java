package io.playqd.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.State;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerEventListener;

import io.playqd.data.Track;
import io.playqd.service.MusicLibrary;

public class Player {

    private static final Logger LOG = LoggerFactory.getLogger(Player.class);

    private static final PlayerObservableProperties PROPERTIES = new PlayerObservableProperties();
    private static final MediaPlayerFactory MEDIA_PLAYER_FACTORY = new MediaPlayerFactory();
    private static final MediaPlayer MEDIA_PLAYER = MEDIA_PLAYER_FACTORY.mediaPlayers().newEmbeddedMediaPlayer();
    private static final MediaListPlayer MEDIA_LIST_PLAYER = newMediaListPlayerInstance();

    static List<Track> ORIGINAL_TRACKS;
    static int START_UP_TRACK_IDX_ON_MANUAL_PLAY_REQUEST = -1;
    static MediaListPlayerEventListener LIST_PLAYER_EVENT_LISTENER;

    static {
        MprisApplication.init(MEDIA_PLAYER, MEDIA_LIST_PLAYER).start();
        MEDIA_PLAYER.events().addMediaPlayerEventListener(
                new MediaPlayerEventAdapterImpl(MEDIA_LIST_PLAYER, PROPERTIES));
        addLoggingListeners();
    }

    public static PlayerObservableProperties properties() {
        return PROPERTIES;
    }

    /**
     * Returns a track that is currently in the player.
     *
     */
    public static Optional<PlayerTrack> playerTrack() {
        return Optional.ofNullable((TrackRef) MEDIA_PLAYER.userData())
                .map(tRef -> new PlayerTrack(MusicLibrary.getTrackById(tRef.track().id()), () -> {
                    var status = PlayerTrackStatus.READY;
                    if (isPlaying()) {
                        status = PlayerTrackStatus.PLAYING;
                    } else if (isPaused()) {
                        status = PlayerTrackStatus.PAUSED;
                    }
                    return status;
                }));
    }

    public static void play(Track track) {
        LOG.info("Requesting track from media list: {} - {}.", track.artistName(), track.name());
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
        if (START_UP_TRACK_IDX_ON_MANUAL_PLAY_REQUEST >= 0) {
            play(START_UP_TRACK_IDX_ON_MANUAL_PLAY_REQUEST);
            START_UP_TRACK_IDX_ON_MANUAL_PLAY_REQUEST = -1;
        } else if (!isPlaying()) {
            MEDIA_PLAYER.controls().setPause(false);
        }
        if (!isPlaying()) {
            MEDIA_PLAYER.controls().play();
        }
    }

    public static boolean isPlaying() {
        return MEDIA_PLAYER.status().isPlaying();
    }

    public static boolean isPaused() {
        return State.PAUSED == MEDIA_PLAYER.status().state();
    }

    public static void setVolume(int volume) {
        MEDIA_PLAYER.audio().setVolume(volume);
    }

    public static FetchMode getFetchMode() {
        return properties().fetchMode().get();
    }

    public static PlaybackMode getPlaybackMode() {
        return properties().playbackMode().get();
    }

    public static void setFetchMode(FetchMode fetchMode) {
        properties().setFetchMode(fetchMode);
        enqueue(new TrackListRequest(0, ORIGINAL_TRACKS, false));
        LOG.info("Media list player fetch mode was set to {}", fetchMode);
    }

    public static void setPlaybackMode(PlaybackMode mode) {
        var playbackMode = switch (mode) {
            case LOOP -> uk.co.caprica.vlcj.player.list.PlaybackMode.LOOP;
            case DEFAULT -> uk.co.caprica.vlcj.player.list.PlaybackMode.DEFAULT;
            case REPEAT -> uk.co.caprica.vlcj.player.list.PlaybackMode.REPEAT;
        };
        MEDIA_LIST_PLAYER.controls().setMode(playbackMode);
        properties().setPlaybackMode(mode);
        LOG.info("Media list player playback mode was set to {}", mode);
    }

    public static void onPaused(Consumer<Boolean> callback) {
        properties().paused().addListener((_, _, paused) -> callback.accept(paused != null && paused));
    }

    public static void onStopped(Consumer<Boolean> callback) {
        properties().stopped().addListener((_, _, stopped) -> callback.accept(stopped != null && stopped));
    }

    public static void onFinished(Consumer<Track> callback) {
        properties().finished().addListener((_, _, finishedTrack) -> callback.accept(finishedTrack));
    }

    public static void onQueueFinished(Runnable callback) {
        properties().queueFinished().addListener((_, _, finished) -> {
            var repeat = MEDIA_PLAYER.controls().getRepeat();
            LOG.info("Queue finished: {}. Repeat enabled: {}", finished, repeat);
            if (finished && !repeat) {
                callback.run();
            }
        });
    }

    public static void onPositionChanged(Consumer<Float> callback) {
        properties().positionChanged().addListener((_, _, newPosition) -> callback.accept(newPosition));
    }

    public static void onTimeChanged(Consumer<Long> callback) {
        properties().timeChanged().addListener((_, _, newTime) -> {
            callback.accept(newTime);
        });
    }

    public static void onPlaybackModeChanged(Consumer<PlaybackMode> callback) {
        properties().playbackMode().addListener((_, _, playbackMode) -> callback.accept(playbackMode));
    }

    public static void onPlayingTrackChanged(Consumer<Track> callback) {
        onPlayingTrackChanged(callback, null);
    }

    public static void onPlayingTrackChanged(Consumer<Track> callback, Runnable defaultIfEmpty) {
        properties().playingTrack().addListener((_, _, playingTrack) -> {
            if (playingTrack == null && defaultIfEmpty != null) {
                defaultIfEmpty.run();
            } else {
                callback.accept(playingTrack);
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

    /**
     *
     * @return mutable list
     */
    @SuppressWarnings("unchecked")
    static List<TrackRef> getPlayerListTrackRefs() {
        return (List<TrackRef>) MEDIA_LIST_PLAYER.userData();
    }

    /**
     * Creates a new queue with these tracks. Previous queue is cleared and player state is reset.
     * If autoPlay is set to true, will play a track at specified index from the queue that has just been created.
     *
     */
    public static void enqueue(TrackListRequest trackListRequest) {
        var enqueueRequest = trackListRequest;
        ORIGINAL_TRACKS = enqueueRequest.tracks();
        if (FetchMode.RANDOM == getFetchMode()) {
            var shuffled = new ArrayList<>(ORIGINAL_TRACKS);
            Collections.shuffle(shuffled);
            enqueueRequest = new TrackListRequest(0, shuffled, enqueueRequest.autoPlay());
        }

        var playTrackRef = enqueueNewList(enqueueRequest);
        preparePlayer(playTrackRef);
        if (enqueueRequest.autoPlay()) {
            play(enqueueRequest.firstTrackPosition());
        } else {
            START_UP_TRACK_IDX_ON_MANUAL_PLAY_REQUEST = enqueueRequest.firstTrackPosition();
        }
    }

    public static void addNext(List<Track> tracks) {
        addToQueue(new TrackListRequest(queueList().size(), tracks)); //todo resolve next idx
    }

    public static void addLast(List<Track> tracks) {
        Player.addToQueue(new TrackListRequest(queueList().size(), tracks));
    }

    public static List<Track> queueList() {
        var trackRefs = getPlayerListTrackRefs();
        if (trackRefs == null) {
            return Collections.emptyList();
        }
        return trackRefs.stream().map(ref -> MusicLibrary.getTrackById(ref.track().id())).toList();
    }

    public static List<Track> originalList() {
        return ORIGINAL_TRACKS == null ? Collections.emptyList() : ORIGINAL_TRACKS;
    }

    static void clear() {
        Player.clearMediaList();
    }

    /**
     * Adds new tracks to the existing queue
     *
     * @param trackListRequest
     */
    static void addToQueue(TrackListRequest trackListRequest) {
        var trackRefs = mapTracksToRefs(trackListRequest.tracks());
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

    public static void remove(List<Track> tracks) {
        if (MEDIA_LIST_PLAYER.list().media() == null) {
            return;
        }
        if (tracks == null || tracks.isEmpty()) {
            return;
        }
        var trackIds = tracks.stream().map(Track::id).toList();
        var userDataTrackRefs = getPlayerListTrackRefs();
        // Indices must be in descending order
        var indices = new TreeSet<Integer>((i1, i2) -> Integer.compare(i2, i1));
        for (int i = 0; i < userDataTrackRefs.size(); i++) {
            if (trackIds.contains(userDataTrackRefs.get(i).track().id())) {
                indices.add(i);
            }
        }
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

    public static void play(int index) {
        MEDIA_LIST_PLAYER.controls().play(index);
    }

    private static void resetListEventListener() {
        // Need to reset the listener when playing by index, otherwise the list listener triggers on previous mrl
        // preventing to grab current mrl correctly.
        // The listener is reset upon playing callback in media player listener.
        if (LIST_PLAYER_EVENT_LISTENER != null) {
            MEDIA_LIST_PLAYER.events().removeMediaListPlayerEventListener(LIST_PLAYER_EVENT_LISTENER);
            LIST_PLAYER_EVENT_LISTENER = null;
        }
    }

    private static TrackRef enqueueNewList(TrackListRequest trackListRequest) {
        var index = trackListRequest.firstTrackPosition();

        var trackRefs = mapTracksToRefs(trackListRequest.tracks());

        MEDIA_LIST_PLAYER.userData(new ArrayList<>(trackRefs));

        var playTrackRef = trackRefs.get(index);

        LOG.info("Created new media list with {} tracks. Playback starts at index: {} ({} - {})",
                trackRefs.size(), index, playTrackRef.track().artistName(), playTrackRef.track().name());

        setNewMediaList(trackRefs);

        return playTrackRef;
    }

    private static void preparePlayer(TrackRef trackRef) {
        resetListEventListener();
        MEDIA_LIST_PLAYER.mediaPlayer().mediaPlayer().userData(trackRef);
    }

    private static void play(int index, TrackRef trackRef) {
        preparePlayer(trackRef);
        play(index);
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

    private static List<TrackRef> mapTracksToRefs(List<Track> tracks) {
        return tracks.stream().map(TrackRef::new).toList();
    }

    private static void addLoggingListeners() {
        onPaused(paused -> LOG.info("{}", paused ? "PAUSED" : "RESUMED"));
        onFinished(finishedTrack ->
                LOG.info("FINISHED PLAYING '{} - {}'", finishedTrack.artistName(), finishedTrack.name()));
        onStopped(stopped -> {
            if (stopped) {
                LOG.info("STOPPED.");
            }
        });
        onPlayingTrackChanged(track -> {
            if (track.isCueTrack()) {
                LOG.info("PLAYING (cue track) '{} - {}'", track.artistName(), track.name());
            } else {
                LOG.info("PLAYING '{} - {}'", track.artistName(), track.name());
            }
        });
    }

    private static MediaListPlayer newMediaListPlayerInstance() {
        var instance = MEDIA_PLAYER_FACTORY.mediaPlayers().newMediaListPlayer();
        instance.mediaPlayer().setMediaPlayer(MEDIA_PLAYER);
        instance.controls().setMode(uk.co.caprica.vlcj.player.list.PlaybackMode.DEFAULT);
        return instance;
    }

}
