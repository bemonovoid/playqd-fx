package io.playqd.controller.library;

import javafx.scene.control.Hyperlink;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

final class AzItemsBuilder {

    static void build(VBox azVBox, Consumer<KeyCode> onAzItemAction) {
        var children = azVBox.getChildren();
        var all = new Hyperlink("All");
        all.setStyle("-fx-font-size: 10");
        all.setOnAction(_ -> onAzItemAction.accept(KeyCode.ESCAPE));
        children.add(all);
        for (char c = 'A'; c <= 'Z'; c++) {
            var str = Character.toString(c);
            var hl = new Hyperlink(str);
            hl.setStyle("-fx-font-size: 10");
            hl.setOnAction(_ -> onAzItemAction.accept(KeyCode.valueOf(str)));
            children.add(hl);
        }
        var numbers = new Hyperlink("0-9");
        numbers.setStyle("-fx-font-size: 10");
        numbers.setOnAction(_ -> onAzItemAction.accept(KeyCode.DIGIT1));
        var misc = new Hyperlink("!#?");
        misc.setStyle("-fx-font-size: 10");
        misc.setOnAction(_ -> onAzItemAction.accept(KeyCode.STAR));
        children.addAll(numbers, misc);
    }
}
