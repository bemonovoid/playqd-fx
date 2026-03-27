package io.playqd.controller;

import io.playqd.dialog.settings.SettingsDialog;
import javafx.fxml.FXML;

public class ApplicationMenuBarController {

    @FXML
    private void showSettingsDialog() {
        new SettingsDialog().afterShowAndWait(_ -> {

        });
    }
}
