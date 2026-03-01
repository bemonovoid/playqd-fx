package io.playqd.player;

import io.playqd.data.Track;
import io.playqd.utils.PlayqdApis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;

public class PlayerEngine {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerEngine.class);

    private static final MediaPlayer MEDIA_PLAYER;
    private static final MediaPlayerFactory MEDIA_PLAYER_FACTORY;
    private static final MediaPlayerEventAdapterImpl MEDIA_PLAYER_EVENT_ADAPTER;

    public static final PlayingQueue PLAYING_QUEUE = new PlayingQueue();

    static {
        MEDIA_PLAYER_FACTORY = new MediaPlayerFactory();
        MEDIA_PLAYER_EVENT_ADAPTER = new MediaPlayerEventAdapterImpl(PLAYING_QUEUE);
        MEDIA_PLAYER = MEDIA_PLAYER_FACTORY.mediaPlayers().newMediaPlayer();
        MEDIA_PLAYER.events().addMediaPlayerEventListener(MEDIA_PLAYER_EVENT_ADAPTER);
    }

    public static PlayerEventConsumerRegistry eventConsumerRegistry() {
        return MEDIA_PLAYER_EVENT_ADAPTER;
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
        PLAYING_QUEUE.next().ifPresent(PlayerEngine::submitPlay);
    }

    public static void seek(float position) {
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

}
