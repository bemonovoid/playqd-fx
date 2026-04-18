package io.playqd.mini.controller.item;

public abstract sealed class UuidLibraryItemRow extends LibraryItemRow permits WatchFolderItemRow, FolderItemRow {

    public abstract String getUUID();
}
