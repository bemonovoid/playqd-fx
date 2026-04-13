package io.playqd.mini.controller.factories;

import io.playqd.mini.controller.item.LibraryItemRow;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ImageTableCellFactory
        implements Callback<TableColumn<LibraryItemRow, Long>, TableCell<LibraryItemRow, Long>> {

    private static final Logger LOG = LoggerFactory.getLogger(ImageTableCellFactory.class);

    private static final int IMAGE_SIZE = 20;

    protected abstract Image getImage(long itemId, int size);

    protected abstract Image getDefaultImage(long itemId, int size, boolean updateCache);

    @Override
    public TableCell<LibraryItemRow, Long> call(TableColumn<LibraryItemRow, Long> param) {

        return new TextFieldTableCell<>() {

            private final ImageView imageView = new ImageView();

            @Override
            public void updateItem(Long trackId, boolean empty) {
                super.updateItem(trackId, empty);
                if (trackId != null || !isEmpty()) {
                    setText(null);
                    var image = getImage(trackId, IMAGE_SIZE);
                    if (image == null) {
                        imageView.setImage(getDefaultImage(trackId, IMAGE_SIZE, false));
                    } else {
                        imageView.setImage(image);
                        image.errorProperty().addListener((_, _, hasError) -> {
                            if (hasError) {
                                var defaultImage = getDefaultImage(trackId, IMAGE_SIZE, true);
                                imageView.setImage(defaultImage);
                            }
                        });
                    }
                    setGraphic(imageView);
                }
            }
        };
    }

}
