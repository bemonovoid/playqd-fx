package io.playqd.mini.controller;

public enum SearchScope {

    ARTISTS("Artists", "a"),

    ALBUMS("Albums", "aa"),

    COLLECTIONS("Collections", "c"),

    FOLDERS("Folders", "f"),

    GENRES("Genres", "g"),

    PLAYLISTS("Playlists", "p"),

    TRACKS("Tracks", "t");

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
