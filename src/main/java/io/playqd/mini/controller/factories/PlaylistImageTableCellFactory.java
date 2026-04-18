package io.playqd.mini.controller.factories;

import io.playqd.client.Images;
import io.playqd.mini.controller.item.LibraryItemRow;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;

public class PlaylistImageTableCellFactory extends ImageTableCellFactory {

    @Override
    public TableCell<LibraryItemRow, Long> call(TableColumn<LibraryItemRow, Long> param) {

        return new TextFieldTableCell<>() {

            @Override
            public void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setGraphic(new ImageView(Images.defaultPlaylist(25)));
//                    setGraphic(new FontAwesomeIconView(FontAwesomeIcon.HEADPHONES));
                }
            }
        };
    }
}
