package io.playqd.controller.view.request;

import io.playqd.controller.view.ApplicationViews;

public record CollectionsViewRequest(long collectionId, String refId) implements ApplicationViewRequest {

    @Override
    public ApplicationViews view() {
        return ApplicationViews.COLLECTIONS;
    }

    @Override
    public CollectionsViewRequest collectionsViewRequest() {
        return this;
    }
}
