package io.playqd.mini.controller.factories;

import io.playqd.client.Images;
import io.playqd.mini.controller.item.LibraryItemRow;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.function.Function;

public class TracksCountTableCellFactory implements MiscValueTableCellFactory {

    private final Function<LibraryItemRow, Integer> countProducer;

    public TracksCountTableCellFactory(Function<LibraryItemRow, Integer> countProducer) {
        this.countProducer = countProducer;
    }

    @Override
    public TableCell<LibraryItemRow, String> call(TableColumn<LibraryItemRow, String> param) {

        return new TextFieldTableCell<>() {

            private final HBox container = new HBox();
            private final Label countLabel = new Label();

            {
                var image = new ImageView(Images.defaultAudioFile());
                container.setSpacing(1);
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
                countLabel.setText(String.valueOf(count));
                setText(null);
                setGraphic(container);
            }
        };
    }
}
