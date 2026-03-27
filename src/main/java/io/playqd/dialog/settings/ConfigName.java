package io.playqd.dialog.settings;

public enum ConfigName {

    GENERAL("General"),

    CACHES("Caches"),

    LIBRARY("Library"),

    DBUS("DBus"),

    SERVER("Server");

    private final String displayName;

    ConfigName(String displayName) {
        this.displayName = displayName;
    }


    public String displayName() {
        return displayName;
    }
}
