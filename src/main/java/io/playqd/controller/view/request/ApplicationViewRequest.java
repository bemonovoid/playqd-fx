package io.playqd.controller.view.request;

import io.playqd.controller.view.ApplicationViews;

public sealed interface ApplicationViewRequest permits FoldersViewRequest, MusicLibraryViewRequest {

    ApplicationViews view();

    default FoldersViewRequest foldersViewRequest() {
        return null;
    }

    default MusicLibraryViewRequest musicLibraryViewRequest() {
        return null;
    }
}
