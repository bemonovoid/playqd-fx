package io.playqd.service;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    public static void showSuccess(String title, String message) {
        Platform.runLater(() -> {
            Notifications.create()
                    .title(title)
                    .text(message)
                    .graphic(null) // You can pass an ImageView here
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.TOP_RIGHT)
                    .onAction(e -> LOG.info("Notification clicked!"))
                    .showInformation(); // Types: showConfirm, showError, showWarning
        });
    }

    public static void showError(String title, String message) {
        Platform.runLater(() -> {
            Notifications.create()
                    .title(title)
                    .text(message)
                    .position(Pos.BOTTOM_RIGHT)
                    .showError();
        });
    }
}
