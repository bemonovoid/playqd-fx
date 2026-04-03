package io.playqd.controller.view.request;

import io.playqd.controller.view.ApplicationViews;

import java.nio.file.Path;

public record FoldersViewRequest(Path location) implements ApplicationViewRequest {

    @Override
    public ApplicationViews view() {
        return ApplicationViews.FOLDERS;
    }

    @Override
    public FoldersViewRequest foldersViewRequest() {
        return this;
    }

}
