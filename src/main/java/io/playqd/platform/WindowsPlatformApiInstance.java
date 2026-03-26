package io.playqd.platform;

import io.playqd.data.WatchFolderItem;
import javafx.scene.image.Image;

class WindowsPlatformApiInstance extends PlatformApiInstance {

    @Override
    Image getSystemIconForFile(WatchFolderItem item) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
