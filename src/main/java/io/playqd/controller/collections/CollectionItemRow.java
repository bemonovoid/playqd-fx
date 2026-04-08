package io.playqd.controller.collections;

import io.playqd.data.MediaCollectionItem;
import io.playqd.data.MediaItemType;
import io.playqd.utils.DateUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class CollectionItemRow {

    private final MediaCollectionItem item;
    private final StringProperty commentProperty = new SimpleStringProperty();

    public CollectionItemRow(MediaCollectionItem item) {
        this.item = item;
        this.commentProperty.set(item.comment());
    }

    public MediaCollectionItem item() {
        return item;
    }

    public String getTitle() {
        return item.title();
    }

    public MediaItemType getItemType() {
        return item.itemType();
    }

    public String getRefId() {
        return item.refId();
    }

    public StringProperty getComment() {
        return commentProperty;
    }

    public String getCreatedDate() {
        if (item.createdDate() == null) {
            return "";
        }
        return DateUtils.ldFormatted(item.createdDate());
    }

    public void setComment(String comment) {
        commentProperty.set(comment);
    }

}
