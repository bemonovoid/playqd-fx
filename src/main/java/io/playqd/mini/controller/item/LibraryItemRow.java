package io.playqd.mini.controller.item;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract sealed class LibraryItemRow permits ArtistItemRow, AlbumItemRow, TrackItemRow, PlaylistItemRow {

    private final StringProperty miscValue;

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

    public final StringProperty getMiscValue() {
        return miscValue;
    }

    public final void setMiscValue(String miscValue) {
        this.miscValue.set(miscValue);
    }

    public abstract Object getSource();

}
