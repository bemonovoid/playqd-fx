package io.playqd.fxml;

public enum FXMLResource {

    APPLICATION("/fxml/application.fxml");
//    APPLICATION_MENU_BAR("/fxml/application-menu-bar.fxml");


    private final String path;

    FXMLResource(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }

}
