package io.playqd.utils;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public final class ClipboardHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ClipboardHelper.class);

    public static void clear() {
        Clipboard.getSystemClipboard().clear();
        LOG.info("Clipboard cleared.");
    }

    public static void putString(String data) {
        clear();
        var content = new ClipboardContent();
        content.putString(data);
        Clipboard.getSystemClipboard().setContent(content);
    }

    public static void putFile(File file) {
        clear();
        var content = new ClipboardContent();
        content.putFiles(List.of(file));
        Clipboard.getSystemClipboard().setContent(content);
    }

}
