package io.playqd.dialog.collection;

import javafx.scene.control.TextInputDialog;

public class CollectionDialog extends TextInputDialog {

    public CollectionDialog() {
        this(null);
    }

    public CollectionDialog(String name) {
        super(name == null ? "" : name);
        setGraphic(null);
        setHeaderText(null);
        setTitle(name != null ? "Rename collection" : "New collection");
        getEditor().setPromptText("New name");
        getDialogPane().setStyle("-fx-background-color: #f4f4f4;");
    }
}
