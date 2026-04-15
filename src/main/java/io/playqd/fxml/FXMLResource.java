package io.playqd.fxml;

public enum FXMLResource {

    APPLICATION("/fxml/application.fxml"),
    APPLICATION_MINI("/fxml/mini/mini-application.fxml"),

    ARTWORK_GALLERY_VIEW("/fxml/artwork-gallery-view.fxml"),

    TRACKS_VIEW("/fxml/tracks-view.fxml"),
    TRACKS_TABLE_HEADER("/fxml/tracks-table-header.fxml"),
    TRACKS_TABLE_VIEW("/fxml/tracks-table-view.fxml"),
    TRACKS_TABLE_FOOTER("/fxml/tracks-table-footer.fxml"),

    DIALOG_SETTINGS("/fxml/dialog/settings/settings.fxml"),
    DIALOG_SETTINGS_SERVER("/fxml/dialog/settings/server-config.fxml"),
    DIALOG_SETTINGS_CACHES("/fxml/dialog/settings/caches-config.fxml"),
    DIALOG_SETTINGS_LIBRARY("/fxml/dialog/settings/library-config.fxml"),
    DIALOG_SETTINGS_DBUS("/fxml/dialog/settings/dbus-config.fxml"),

    DIALOG_TRACKS_TABLE_VIEW_COLUMNS("/fxml/dialog/tracks-table-view-columns-dialog-pane.fxml"),

    ALBUMS_HEADER_MENU_BUTTON("/fxml/mini/albums-header-menu-btn.fxml");

    private final String path;

    FXMLResource(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }

}
