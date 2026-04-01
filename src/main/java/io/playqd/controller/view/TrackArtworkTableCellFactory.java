package io.playqd.controller.view;

import io.playqd.client.ArtworkImages;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TrackArtworkTableCellFactory implements Callback<TableColumn<TrackModel, Long>, TableCell<TrackModel, Long>> {

    private static final Logger LOG = LoggerFactory.getLogger(TrackArtworkTableCellFactory.class);

    @Override
    public TableCell<TrackModel, Long> call(TableColumn<TrackModel, Long> param) {

        return new TextFieldTableCell<>() {

            private final ImageView imageView = new ImageView();

            @Override
            public void updateItem(Long trackId, boolean empty) {
                super.updateItem(trackId, empty);
                if (trackId != null || !isEmpty()) {
                    setText(null);
                    var image = ArtworkImages.album(trackId, 25);
                    if (image == null) {
                        imageView.setImage(ArtworkImages.defaultAlbum(25));
                    } else {
                        imageView.setImage(image);
                        image.errorProperty().addListener((_, _, hasError) -> {
                            if (hasError) {
                                var defaultImage = ArtworkImages.defaultAlbum(25);
                                ArtworkImages.setAlbum(trackId, defaultImage, 25);
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
