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
    private final StringProperty status = new SimpleStringProperty("");

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

    public final StringProperty getStatus() {
        return status;
    }

    public final StringProperty getMiscValue() {
        return miscValue;
    }

    public final void setStatus(String status) {
        this.status.set(status);
    }

    public final void setMiscValue(String miscValue) {
        this.miscValue.set(miscValue);
    }

    public abstract Object getSource();

}
