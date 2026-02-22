package io.playqd.fxml;

public enum FXMLResource {

    APPLICATION("/fxml/application.fxml"),

    TRACKS_TABLE_VIEW("/fxml/tracks-table-view.fxml"),

    TRACKS_CONTAINER("/fxml/tracks-container.fxml"),

    DIALOG_INVALIDATE_CACHES("/fxml/dialog/invalidate-caches.fxml"),

    DIALOG_TRACKS_TABLE_VIEW_COLUMNS("/fxml/dialog/tracks-table-view-columns-dialog-pane.fxml");

    private final String path;

    FXMLResource(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }

}
