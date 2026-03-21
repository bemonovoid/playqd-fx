package io.playqd.platform;

import javafx.application.HostServices;

public abstract class PlatformApiImpl {

    private HostServices hostServices;

    protected final HostServices getHostServices() {
        return hostServices;
    }

    protected final void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }
}
