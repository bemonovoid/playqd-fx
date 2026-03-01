package io.playqd.utils;

import io.playqd.config.AppConfig;
import io.playqd.data.Track;
import io.playqd.service.TracksService;

public class PlayqdApis {

    public static String baseUrl() {
        return AppConfig.getProperties().apiBaseUrl().get();
    }

    public static String albumArtwork(long audioFileId) {
        return albumArtwork(TracksService.getTrackById(audioFileId));
    }

    public static String albumArtwork(Track track) {
        var audioFileId = track.id();
        if (track.cueInfo().parentId() != null) {
            audioFileId = track.cueInfo().parentId();
        }
        return baseUrl() + "/albums/" + audioFileId + "/artwork";
    }

}
