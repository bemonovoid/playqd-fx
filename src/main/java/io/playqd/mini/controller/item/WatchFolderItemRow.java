package io.playqd.mini.controller.item;

import io.playqd.data.WatchFolder;

public final class WatchFolderItemRow extends UuidLibraryItemRow {

    private final WatchFolder watchFolder;

    public WatchFolderItemRow(WatchFolder watchFolder) {
        this.watchFolder = watchFolder;
    }

    @Override
    public long getId() {
        return watchFolder.uuid().hashCode();
    }

    @Override
    public String getName() {
        return watchFolder.name();
    }

    @Override
    public String getDescription() {
        return watchFolder.location();
    }

    @Override
    public WatchFolder getSource() {
        return watchFolder;
    }

    @Override
    public String getUUID() {
        return watchFolder.uuid();
    }
}
