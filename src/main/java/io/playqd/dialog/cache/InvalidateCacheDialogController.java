package io.playqd.dialog.cache;

import io.playqd.dialog.PlayqdAbstractDialogController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;

public class InvalidateCacheDialogController extends
        PlayqdAbstractDialogController<InvalidateCacheDialog.InvalidateCacheDialogPane> {

    @FXML
    private CheckBox mediaLibraryServerCacheCheckBox;

    @FXML
    private ButtonType invalidateAndRefreshBtnType;

    private Button invalidateAndRefreshBtn;

    @FXML
    private void initialize() {
        var invalidateAndRefreshBtn = ((Button) dialogPane.lookupButton(invalidateAndRefreshBtnType));
    }

}
