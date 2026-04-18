package io.playqd.mini.controller.factories;

import io.playqd.client.Images;
import io.playqd.data.MediaItemType;
import io.playqd.mini.controller.item.CollectionChildItemRow;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.TrackItemRow;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import org.kordamp.ikonli.javafx.FontIcon;

public final class CollectionItemImageTableCellFactory extends ImageTableCellFactory {

    @Override
    public TableCell<LibraryItemRow, Long> call(TableColumn<LibraryItemRow, Long> param) {

        return new TextFieldTableCell<>() {

            @Override
            public void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty && getTableRow().getItem() instanceof CollectionChildItemRow collectionItem) {
                    if (MediaItemType.TRACK == collectionItem.getSource().itemType()) {
                        setGraphic(new ImageView(Images.defaultAudioFile()));
                    } else {
                        setGraphic(new FontIcon("far-file-alt"));
                    }
                }
            }
        };
    }
}
