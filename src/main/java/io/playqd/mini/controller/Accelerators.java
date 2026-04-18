package io.playqd.mini.controller;

import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        createQuickNavigationAccelerator(scene);
        createRequestSearchInputFocusAccelerator(scene);
        INITIALIZED = true;
    }

    private static void createQuickNavigationAccelerator(Scene scene) {
        var keyCodeCombination = new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(keyCodeCombination,  () -> {
            var menuBtn = (MenuButton) scene.lookup("#" + QUICK_NAV_ITEMS_MENU_BTN);
            menuBtn.fire();
//            var bounds = menuBtn.localToScreen(menuBtn.getBoundsInLocal());
//            double centerX = bounds.getMinX() + (bounds.getWidth() / 2);
//            double centerY = bounds.getMinY() + (bounds.getHeight() / 2);
//            var contextMenu = new ContextMenu();
//            contextMenu.getStyleClass().add("nav-context-menu");
//            contextMenu.getItems().addAll(QuickNavigationMenuItems.get(menuBtn));
//            contextMenu.show(menuBtn, centerX, centerY);
        });
    }



    private static void createRequestSearchInputFocusAccelerator(Scene scene) {
        var keyCodeCombination = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(keyCodeCombination,  () ->
                scene.lookup("#" + SEARCH_INPUT_TEXT_FLD_ID).requestFocus());
    }
}
