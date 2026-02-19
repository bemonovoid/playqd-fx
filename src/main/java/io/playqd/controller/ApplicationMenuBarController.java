package io.playqd.controller;

import io.playqd.dialog.cache.InvalidateCacheDialog;
import javafx.fxml.FXML;

public class ApplicationMenuBarController {

    @FXML
    private void showInvalidateCachesDialog() {
        new InvalidateCacheDialog().afterShowAndWait(o -> {

        });
    }
}
