package io.playqd.mini.custom;

import io.playqd.mini.controller.item.*;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

public class ConfirmDeleteRowItemsDialog {

    public static <T extends LibraryItemRow> boolean confirmDelete(List<T> items) {

        var warnIcon = new FontIcon("fas-exclamation-triangle");
        warnIcon.setIconSize(18);
        warnIcon.setIconColor(Color.web("#e74c3c")); // Minimalist red

        var deleteMessage = "Delete ";
        var item = items.getFirst();
        if (item instanceof PlaylistItemRow p) {
            if (items.size() > 1) {
                deleteMessage += String.format("%s playlists", items.size());
            } else {
                deleteMessage += "'" + p.getName() + "' playlist";
            }
        } else if (item instanceof PlaylistTrackItemRow pt) {
            deleteMessage += String.format("%s playlist track%s", items.size(), items.size() > 1 ? "s" :"");
        } else if (item instanceof CollectionItemRow c) {
            if (items.size() > 1) {
                deleteMessage += String.format("%s collections", items.size());
            } else {
                deleteMessage += "'" + c.getName() + "' collection";
            }
        } else if (item instanceof CollectionChildItemRow ci) {
            deleteMessage += String.format("%s collection item%s", items.size(), items.size() > 1 ? "s" :"");
        } else {
            deleteMessage += "unknown";
        }
        deleteMessage += "?";

        var contentLabel = new Label(deleteMessage);

        contentLabel.setGraphic(warnIcon);
        contentLabel.setGraphicTextGap(10);
        contentLabel.setStyle("-fx-font-size: 14px; -fx-padding: 20 20 20 20;");

        var dialog = new Dialog<ButtonType>();
        dialog.setTitle("Delete items");
        dialog.getDialogPane().setContent(contentLabel);

        // 4. Add Minimalist Buttons
        var cancelButtonType = new ButtonType("Cancel");
        var deleteButtonType = new ButtonType("Delete");

        dialog.getDialogPane().getButtonTypes().addAll(cancelButtonType, deleteButtonType);

        var cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.setCancelButton(true);

        var deleteButton = (Button) dialog.getDialogPane().lookupButton(deleteButtonType);
        deleteButton.setDefaultButton(true);
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        var buttonTypeOpt = dialog.showAndWait();

        return buttonTypeOpt.isPresent() && buttonTypeOpt.get() == deleteButtonType;

    }
}
