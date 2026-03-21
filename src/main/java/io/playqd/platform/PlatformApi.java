package io.playqd.platform;

import io.playqd.platform.linux.LinuxMintPlatformApi;
import io.playqd.platform.linux.LinuxPlatformApi;
import io.playqd.platform.linux.LsbRelease;
import io.playqd.platform.windows.WindowsPlatformApi;

public abstract class PlatformApi {

    private static final PlatformApiImpl INSTANCE;

    static {
        if (isLinux()) {
            var isLinuxMint = LsbRelease.get()
                    .map(LsbRelease::isMint)
                    .orElse(false);
            if (isLinuxMint) {
                INSTANCE = new LinuxMintPlatformApi();
            } else {
                INSTANCE = new LinuxPlatformApi();
            }
        } else if (isWindows()) {
            INSTANCE = new WindowsPlatformApi();
        } else {
            throw new IllegalStateException("Unsupported OS");
        }
    }

    public static boolean isWindows() {
        // PlatformUtil.isWindows() sometimes fails in Win 11
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }
}
