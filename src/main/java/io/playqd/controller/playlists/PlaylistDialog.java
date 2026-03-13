package io.playqd.controller.playlists;

import javafx.scene.control.TextInputDialog;

public class PlaylistDialog extends TextInputDialog {

    public PlaylistDialog() {
        this(null);
    }

    public PlaylistDialog(String name) {
        super(name == null ? "" : name);
        setGraphic(null);
        setHeaderText(null);
        setTitle(name != null ? "Rename playlist" : "New playlist");
        getEditor().setPromptText("New name");
        getDialogPane().setStyle("-fx-background-color: #f4f4f4;");
    }
}
