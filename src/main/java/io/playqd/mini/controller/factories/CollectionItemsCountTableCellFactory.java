package io.playqd.mini.controller.factories;

import io.playqd.mini.controller.item.LibraryItemRow;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.function.Function;

public final class CollectionItemsCountTableCellFactory implements MiscValueTableCellFactory {

    private final Function<LibraryItemRow, Integer> countProducer;

    public CollectionItemsCountTableCellFactory(Function<LibraryItemRow, Integer> countProducer) {
        this.countProducer = countProducer;
    }

    @Override
    public TableCell<LibraryItemRow, String> call(TableColumn<LibraryItemRow, String> param) {
        return new TextFieldTableCell<>() {

            private final HBox container = new HBox();
            private final Label countLabel = new Label();
            private final Node image = new FontIcon("far-file");

            {
                container.setSpacing(3);
                container.setAlignment(Pos.CENTER_LEFT);
                container.getChildren().addAll(image, countLabel);
            }

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    return;
                }
                var count = countProducer.apply(getTableRow().getItem());
                setText(null);
                if (count <= 0) {
                    var hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    hBox.getChildren().addAll(new Label("<empty>"));
                    setGraphic(hBox);
                } else {
                    countLabel.setText(String.valueOf(count));
                    setGraphic(container);
                }
            }
        };
    }
}
