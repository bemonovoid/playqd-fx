package io.playqd.mini.controller;

public enum SearchScope {

    ARTISTS("Artists", "a"),

    ALBUMS("Albums", "aa"),

    TRACKS("Tracks", "t"),

    PLAYLISTS("Playlists", "p"),

    COLLECTIONS("Collections", "c");

    private final String displayText;
    private final String shortcut;

    SearchScope(String displayText, String shortcut) {
        this.displayText = displayText;
        this.shortcut = shortcut;
    }

    public String displayText() {
        return displayText;
    }

    public String shortcut() {
        return shortcut;
    }
}
