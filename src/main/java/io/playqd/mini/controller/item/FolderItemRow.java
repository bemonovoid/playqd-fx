package io.playqd.mini.controller.item;

import io.playqd.data.WatchFolderItem;

public final class FolderItemRow extends UuidLibraryItemRow {

    private final WatchFolderItem watchFolderItem;

    public FolderItemRow(WatchFolderItem watchFolderItem) {
        this.watchFolderItem = watchFolderItem;
    }

    @Override
    public long getId() {
        return watchFolderItem.hashCode();
    }

    @Override
    public String getName() {
        return watchFolderItem.name();
    }

    @Override
    public WatchFolderItem getSource() {
        return watchFolderItem;
    }

    @Override
    public String getUUID() {
        return watchFolderItem.id();
    }
}
