package io.playqd.platform;

import io.playqd.data.WatchFolderItem;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LinuxPlatformApiInstance extends PlatformApiInstance {

    private static final Logger LOG = LoggerFactory.getLogger(LinuxPlatformApiInstance.class);

    @Override
    Image getSystemIconForFile(WatchFolderItem item) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
