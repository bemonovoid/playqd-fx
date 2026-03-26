package io.playqd.controller;

import io.playqd.dialog.cache.InvalidateCacheDialog;
import io.playqd.dialog.settings.SettingsDialog;
import javafx.fxml.FXML;

public class ApplicationMenuBarController {

    @FXML
    private void showInvalidateCachesDialog() {
        new InvalidateCacheDialog().afterShowAndWait(o -> {

        });
    }

    @FXML
    private void showSettingsDialog() {
        new SettingsDialog().afterShowAndWait(_ -> {

        });
    }
}
