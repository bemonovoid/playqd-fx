package io.playqd.dialog;

import javafx.scene.control.DialogPane;

public abstract class PlayqdDialogPane<D extends PlayqdDialog<?>, C extends PlayqdDialogController> extends DialogPane {

    protected PlayqdDialogPane() {
        getStylesheets().add("/css/glyphs.css");
    }

    private D dialog;
    private C controller;

    public final void setDialog(D dialog) {
        this.dialog = dialog;
    }

    public final void setResult(Object result) {
        dialog.setResult(result);
    }

    final C getController() {
        return controller;
    }

    public final void setController(C controller) {
        this.controller = controller;
    }

}
