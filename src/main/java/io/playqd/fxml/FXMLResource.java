package io.playqd.fxml;

public enum FXMLResource {

    APPLICATION("/fxml/application.fxml"),

    ARTISTS_TAB("/fxml/artists-tab.fxml"),
    TRACKS_TAB("/fxml/tracks-tab.fxml");

    private final String path;

    FXMLResource(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }

}
