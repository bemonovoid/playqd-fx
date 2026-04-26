package io.playqd.mini.controller;

import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.playqd.dialog.settings.SettingsDialog;

public final class Accelerators {

    private static final Logger LOGGER = LoggerFactory.getLogger(Accelerators.class);

    private static final String SEARCH_INPUT_TEXT_FLD_ID = "searchInputTextFld";
    private static final String QUICK_NAV_ITEMS_MENU_BTN = "quickNavItemsMenuBtn";

    private static boolean INITIALIZED = false;

    private Accelerators() {

    }

    public static void initialize(Scene scene) {
        if (INITIALIZED) {
            LOGGER.warn("Accelerators already initialized");
            return;
        }
        createSettingsDialogAccelerator(scene);
        createQuickNavigationAccelerator(scene);
        createRequestSearchInputFocusAccelerator(scene);
        INITIALIZED = true;
    }

    private static void createSettingsDialogAccelerator(Scene scene) {
        var keyCodeCombination = new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN);
        scene.getAccelerators().put(keyCodeCombination,  () -> new SettingsDialog().afterShowAndWait(_ -> {}));
    }

    private static void createQuickNavigationAccelerator(Scene scene) {
        var keyCodeCombination = new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(keyCodeCombination,  () -> {
            var menuBtn = (MenuButton) scene.lookup("#" + QUICK_NAV_ITEMS_MENU_BTN);
            menuBtn.fire();
        });
    }

    private static void createRequestSearchInputFocusAccelerator(Scene scene) {
        var keyCodeCombination = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(keyCodeCombination,  () ->
                scene.lookup("#" + SEARCH_INPUT_TEXT_FLD_ID).requestFocus());
    }
}
