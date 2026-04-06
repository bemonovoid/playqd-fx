package io.playqd.controller.view.request;

import io.playqd.controller.view.ApplicationViews;

public sealed interface ApplicationViewRequest permits
        MusicLibraryViewRequest, PlaylistsViewRequest, CollectionsViewRequest, FoldersViewRequest {

    ApplicationViews view();

    default MusicLibraryViewRequest musicLibraryViewRequest() {
        return null;
    }

    default PlaylistsViewRequest playlistsViewRequest() {
        return null;
    }

    default CollectionsViewRequest collectionsViewRequest() {
        return null;
    }

    default FoldersViewRequest foldersViewRequest() {
        return null;
    }
}
