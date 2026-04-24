package io.playqd.mini.controller.factories;

import io.playqd.mini.controller.item.LibraryItemRow;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RemoteImageTableCellFactory extends ImageTableCellFactory {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteImageTableCellFactory.class);

    private static final int IMAGE_SIZE = 30;

    protected abstract Image getImage(LibraryItemRow row, int size);

    @Override
    public TableCell<LibraryItemRow, Long> call(TableColumn<LibraryItemRow, Long> param) {

        return new TableCell<>() {

            private final ImageView imageView = new ImageView();

            {
                imageView.setCache(true);
            }

            @Override
            public void updateItem(Long itemId, boolean empty) {
                super.updateItem(itemId, empty);
                if (itemId != null && !isEmpty()) {
                    imageView.setImage(getImage(getTableRow().getItem(), IMAGE_SIZE));
                    setGraphic(imageView);
                }
            }
        };
    }
}
