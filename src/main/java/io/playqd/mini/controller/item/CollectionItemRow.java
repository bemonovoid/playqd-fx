package io.playqd.mini.controller.item;

import io.playqd.data.MediaCollection;
import javafx.beans.property.SimpleStringProperty;

public final class CollectionItemRow extends LibraryItemRow {

    private final MediaCollection mediaCollection;

    public CollectionItemRow(MediaCollection mediaCollection) {
        super(new SimpleStringProperty("" + mediaCollection.items().size()));
        this.mediaCollection = mediaCollection;
    }

    @Override
    public long getId() {
        return mediaCollection.id();
    }

    @Override
    public String getName() {
        return mediaCollection.name();
    }

    @Override
    public MediaCollection getSource() {
        return mediaCollection;
    }
}
