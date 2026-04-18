package io.playqd.platform;

import io.playqd.data.ItemType;
import io.playqd.data.WatchFolderItem;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

final class LinuxMintPlatformApiInstance extends LinuxPlatformApiInstance {

    private static final Logger LOG = LoggerFactory.getLogger(LinuxMintPlatformApiInstance.class);

    private final Path LINUX_MINT_X_ICONS_DIR = Paths.get("/usr/share/icons/Mint-X");
    private static final String DEFAULT_SIZE = "24";
    private static final int DEFAULT_IMAGE_SIZE = 15;
//    private static final Path LINUX_MINT_L_ICONS_DIR = Paths.get("/usr/share/icons/Mint-L");
//    private static final Path LINUX_MINT_L_YELLOW_ICONS_DIR = Paths.get("/usr/share/icons/Mint-L-Yellow");

    @Override
    Image getSystemIconForFile(WatchFolderItem item) {
        if (ItemType.FOLDER == item.itemType()) {
           throw new UnsupportedOperationException("Folder system icon is not supported");
        }

        var mimeType = item.mimeType();

        if (MIME_TYPE_ICONS.containsKey(mimeType)) {
            return MIME_TYPE_ICONS.get(mimeType);
        }

        if (!mimeType.isBlank()) {
            var mimeTypeAsFilename = mimeType.replaceAll("/", "-");
            var image = getImageFromMimeTypeFile(LINUX_MINT_X_ICONS_DIR, mimeTypeAsFilename);
            MIME_TYPE_ICONS.put(mimeType, image);
            return image;
        }
        LOG.warn("Could not resolve icon for '{}'", item.location());
        return null;
    }

    private static Image getImageFromMimeTypeFile(Path dir, String filename) {
        var path = dir.resolve("mimetypes", DEFAULT_SIZE, filename + ".png");
        if (Files.exists(path)) {
            return new Image(path.toUri().toString(), DEFAULT_IMAGE_SIZE, DEFAULT_IMAGE_SIZE, true, true, true);
        }
        return tryResolveImageForMissingMimetypeFile(filename, dir);
    }

    private static Image tryResolveImageForMissingMimetypeFile(String filename, Path dir) {
        var resolvedFilename = "";
        if (filename.startsWith("application-")) {
            if (filename.charAt("application-".length()) != 'x') {
                resolvedFilename = "application-x-" + filename.substring("application-".length());
                if (!Files.exists(dir.resolve("mimetypes", DEFAULT_SIZE, resolvedFilename + ".png"))) {
                    resolvedFilename = "";
                }
            }
        }
        if (resolvedFilename.isBlank()) {
            if (filename.equalsIgnoreCase("application-java-vm")) {
                resolvedFilename = "application-x-java";
            } else if (filename.startsWith("audio")) {
                if (filename.equalsIgnoreCase("audio-x-flac")) {
                    resolvedFilename = "audio-x-flac+ogg";
                } else {
                    resolvedFilename = "audio-x-generic";
                }
            } else {
                resolvedFilename = "unknown";
            }
        }
        var path = dir.resolve("mimetypes", DEFAULT_SIZE, resolvedFilename + ".png");
        return new Image(path.toUri().toString(), DEFAULT_IMAGE_SIZE, DEFAULT_IMAGE_SIZE,  true, true, true);
    }
}
