package io.playqd.dialog;

import javafx.fxml.FXML;

public abstract class PlayqdAbstractDialogController<DIALOG_PANE extends PlayqdDialogPane<?, ?>>
        implements PlayqdDialogController {

    @FXML
    protected DIALOG_PANE dialogPane;

}
