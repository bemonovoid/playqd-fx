package io.playqd.mini.controller;

import io.playqd.dialog.settings.SettingsDialog;
import javafx.fxml.FXML;

public class MiniMenuBarController {

    @FXML
    private void showSettingsDialog() {
        new SettingsDialog().afterShowAndWait(_ -> {

        });
    }
}
