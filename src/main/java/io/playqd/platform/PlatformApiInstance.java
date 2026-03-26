package io.playqd.platform;

import io.playqd.data.WatchFolderItem;
import javafx.scene.image.Image;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract class PlatformApiInstance {

    protected final Map<String, Image> MIME_TYPE_ICONS = new ConcurrentHashMap<>();

    abstract Image getSystemIconForFile(WatchFolderItem item);
}
