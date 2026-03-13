package io.playqd.player;

import io.playqd.data.Track;
import io.playqd.utils.PlayqdApis;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;

import java.util.function.Consumer;

public class Player {

    private static final Logger LOG = LoggerFactory.getLogger(Player.class);

    private static final MediaPlayer MEDIA_PLAYER;
    private static final MediaPlayerFactory MEDIA_PLAYER_FACTORY;
    private static final MediaPlayerEventAdapterImpl MEDIA_PLAYER_EVENT_ADAPTER;

    static final SimpleObjectProperty<Track> FINISHED_PROPERTY = new SimpleObjectProperty<>();
    static final SimpleObjectProperty<Track> PLAYING_TRACK_PROPERTY = new SimpleObjectProperty<>();
    static final SimpleObjectProperty<Float> POSITION_CHANGED_PROPERTY = new SimpleObjectProperty<>();
    static final SimpleBooleanProperty PAUSED_PROPERTY = new SimpleBooleanProperty();
    static final SimpleBooleanProperty STOPPED_PROPERTY = new SimpleBooleanProperty();
    static final SimpleObjectProperty<Long> TIME_CHANGED_PROPERTY = new SimpleObjectProperty<>();

    public static final PlayerQueue PLAYING_QUEUE = new PlayerQueue();

    static {
        MEDIA_PLAYER_FACTORY = new MediaPlayerFactory();
        MEDIA_PLAYER_EVENT_ADAPTER = new MediaPlayerEventAdapterImpl(PLAYING_QUEUE);
        MEDIA_PLAYER = MEDIA_PLAYER_FACTORY.mediaPlayers().newMediaPlayer();
        MEDIA_PLAYER.events().addMediaPlayerEventListener(MEDIA_PLAYER_EVENT_ADAPTER);
        addLoggingListeners();
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

    public static void enqueueAndPlay(PlayRequest playRequest) {
        PLAYING_QUEUE.clear();
        PLAYING_QUEUE.enqueue(playRequest.tracks());
        PLAYING_QUEUE.setStartingPosition(playRequest.startTrackIdx());
        PLAYING_QUEUE.setVisited(playRequest.startTrackIdx());
        var playingTrack = playRequest.tracks().get(playRequest.startTrackIdx());
        submitPlay(playingTrack);
    }

    public static void playFromQueue(int position) {
        PLAYING_QUEUE.get(position).ifPresent(track -> {
            PLAYING_QUEUE.setStartingPosition(position);
            PLAYING_QUEUE.resetVisited();
            submitPlay(track);
        });
    }

    public static void playNext() {
        if (isPlaying()) {
            MEDIA_PLAYER.controls().stop();
        }
        LOG.info("Resolving next track ...");
        PLAYING_QUEUE.next().ifPresent(Player::submitPlay);
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

    public static void setPlaylistFetchMode(FetchMode fetchMode) {
        PLAYING_QUEUE.setFetchMode(fetchMode);
    }

    public static void setPlaylistLoopMode(LoopMode loopMode) {
        PLAYING_QUEUE.setLoopMode(loopMode);
    }

    private static void submitPlay(Track track) {
        var trackId = track.id();
        var options = new String[]{};
        if (track.cueInfo().parentId() != null) {
            trackId = track.cueInfo().parentId();
            options = buildRangeOptions(track);
        }
        MEDIA_PLAYER.media().play(PlayqdApis.baseUrl() + "/tracks/" + trackId + "/file", options);
    }

    private static String[] buildRangeOptions(Track track) {
        var startTime = track.cueInfo().startTimeInSeconds();
        var endTime = track.cueInfo().startTimeInSeconds() + track.length().seconds();
        var startTimeOption = ":start-time=" + startTime;
        var stopTimeOption = ":stop-time=" + endTime;
        return new String[]{ startTimeOption, stopTimeOption };
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
            if (track.cueInfo().parentId() != null) {
                LOG.info("PLAYING (cue track) '{} - {}'", track.artistName(), track.title());
            } else {
                LOG.info("PLAYING '{} - {}'", track.artistName(), track.title());
            }
        });
    }

}
