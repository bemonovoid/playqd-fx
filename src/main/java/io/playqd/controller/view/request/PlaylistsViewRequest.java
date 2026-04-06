package io.playqd.controller.view.request;

import io.playqd.controller.view.ApplicationViews;

public record PlaylistsViewRequest(long playlistId, Long trackId) implements ApplicationViewRequest {

    @Override
    public ApplicationViews view() {
        return ApplicationViews.PLAYLISTS;
    }

    @Override
    public PlaylistsViewRequest playlistsViewRequest() {
        return this;
    }
}
