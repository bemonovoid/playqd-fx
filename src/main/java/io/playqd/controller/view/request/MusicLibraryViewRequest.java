package io.playqd.controller.view.request;

import io.playqd.controller.view.ApplicationViews;
import io.playqd.data.Track;


public record MusicLibraryViewRequest(Track track) implements ApplicationViewRequest {

    @Override
    public ApplicationViews view() {
        return ApplicationViews.MUSIC_LIBRARY;
    }

    @Override
    public MusicLibraryViewRequest musicLibraryViewRequest() {
        return this;
    }

}
