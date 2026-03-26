package io.playqd.platform;

import io.playqd.data.WatchFolderItem;
import javafx.application.HostServices;
import javafx.scene.image.Image;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class PlatformApi {

    private static final PlatformApiInstance INSTANCE;
    private static HostServices HOST_SERVICES;

    static {
        if (isLinux()) {
            var isLinuxMint = LsbRelease.get().map(LsbRelease::isMint).orElse(false);
            if (isLinuxMint) {
                INSTANCE = new LinuxMintPlatformApiInstance();
            } else {
                INSTANCE = new LinuxPlatformApiInstance();
            }
        } else if (isWindows()) {
            INSTANCE = new WindowsPlatformApiInstance();
        } else {
            throw new IllegalStateException("Unsupported OS");
        }
    }

    private PlatformApi() {

    }

    public static void setHostServices(HostServices hostServices) {
        if (HOST_SERVICES == null) {
            HOST_SERVICES = hostServices;
        }
    }

    public static Path getUserHomeDir() {
        return Paths.get(System.getProperty("user.home"));
    }

    public static boolean isWindows() {
        // PlatformUtil.isWindows() sometimes fails in Win 11
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    public static void open(String location) {
        HOST_SERVICES.showDocument(location);
    }

    public static Image getSystemIconForFile(WatchFolderItem item) {
        return INSTANCE.getSystemIconForFile(item);
    }
}
