package io.playqd.event;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public final class MouseEventHelper {

    public static boolean primaryButtonSingleClicked(MouseEvent mouseEvent) {
        return MouseButton.PRIMARY == mouseEvent.getButton() && mouseEvent.getClickCount() == 1;
    }

    public static boolean primaryButtonDoubleClicked(MouseEvent mouseEvent) {
        return MouseButton.PRIMARY == mouseEvent.getButton() && mouseEvent.getClickCount() == 2;
    }

    public static boolean secondaryButtonSingleClicked(MouseEvent mouseEvent) {
        return MouseButton.SECONDARY == mouseEvent.getButton() && mouseEvent.getClickCount() == 1;
    }

    private MouseEventHelper() {

    }

}
