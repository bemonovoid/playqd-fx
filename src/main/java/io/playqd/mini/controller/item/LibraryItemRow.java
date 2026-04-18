package io.playqd.mini.controller.item;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract sealed class LibraryItemRow permits
        UuidLibraryItemRow,
        ArtistItemRow,
        AlbumItemRow,
        TrackItemRow,
        PlaylistItemRow,
        CollectionItemRow,
        CollectionChildItemRow {

    private final StringProperty miscValue;
    private final StringProperty tags = new SimpleStringProperty("");

    protected LibraryItemRow() {
        this(new SimpleStringProperty(""));
    }

    protected LibraryItemRow(StringProperty miscValue) {
        this.miscValue = miscValue;
    }

    public abstract long getId();

    public abstract String getName();

    public String getDescription() {
        return "";
    }

    public final StringProperty getTags() {
        return tags;
    }

    public final StringProperty getMiscValue() {
        return miscValue;
    }

    public final void setMiscValue(String miscValue) {
        this.miscValue.set(miscValue);
    }

    public final void setTags(String tags) {
        this.tags.set(tags);
    }

    public abstract Object getSource();

}
