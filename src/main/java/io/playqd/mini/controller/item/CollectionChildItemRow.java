package io.playqd.mini.controller.item;

import io.playqd.data.MediaCollectionItem;
import javafx.beans.property.SimpleStringProperty;

public final class CollectionChildItemRow extends LibraryItemRow {

    private final MediaCollectionItem collectionItem;

    public CollectionChildItemRow(MediaCollectionItem collectionItem) {
        super(new SimpleStringProperty(collectionItem.itemType().name()));
        this.collectionItem = collectionItem;
    }

    @Override
    public long getId() {
        return collectionItem.id();
    }

    @Override
    public String getName() {
        return collectionItem.name();
    }

    @Override
    public String getDescription() {
        return collectionItem.comment();
    }

    @Override
    public MediaCollectionItem getSource() {
        return collectionItem;
    }
}
