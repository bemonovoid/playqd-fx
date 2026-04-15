package io.playqd.controller.collections;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.client.Images;
import io.playqd.client.PlayqdClientProvider;
import io.playqd.data.MediaItemType;
import io.playqd.data.WatchFolderItem;
import io.playqd.platform.PlatformApi;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CollectionItemIconTableCellFactory
        implements Callback<TableColumn<CollectionItemRow, String>, TableCell<CollectionItemRow, String>> {

    private static final Logger LOG = LoggerFactory.getLogger(CollectionItemIconTableCellFactory.class);

    @Override
    public TableCell<CollectionItemRow, String> call(TableColumn<CollectionItemRow, String> param) {

        return new TextFieldTableCell<>() {

            private static final int IMAGE_SIZE = 25;

            private final ImageView imageView = new ImageView();

            @Override
            public void updateItem(String item, boolean empty) {

                super.updateItem(item, empty);

                if (item != null && !isEmpty()) {
                    setText(null);
                    var collectionItem = getTableRow().getItem().item();
                    if (MediaItemType.ARTWORK == collectionItem.itemType() ||
                            MediaItemType.TRACK == collectionItem.itemType()) {
                        try {
                            var trackId = collectionItem.refId() != null ? Long.parseLong(collectionItem.refId()) : -1;
                            if (trackId > 0) {
                                var image = Images.album(trackId, IMAGE_SIZE);
                                if (image == null) {
                                    imageView.setImage(Images.defaultAlbum(IMAGE_SIZE));
                                } else {
                                    imageView.setImage(image);
                                    image.errorProperty().addListener((_, _, hasError) -> {
                                        if (hasError) {
                                            var defaultImage = Images.defaultAlbum(IMAGE_SIZE);
                                            Images.setAlbum(trackId, defaultImage, IMAGE_SIZE);
                                            imageView.setImage(defaultImage);
                                        }
                                    });
                                }
                                setGraphic(imageView);
                            }
                        } catch (NumberFormatException e) {

                        }
                    } else if (MediaItemType.FILE == collectionItem.itemType()) {
                        var wfi = PlayqdClientProvider.get().getWatchFolderItemByLocation(collectionItem.refId());
                        setIcon(wfi);
                    }

                }
            }

            private void setIcon(WatchFolderItem item) {
                if (item == null) {
                    return;
                }
                var iconImage = PlatformApi.getSystemIconForFile(item);
                if (iconImage != null) {
                    setGraphic(new ImageView(iconImage));
                } else {
                    setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE_ALT, "25px"));
                }
            }
        };
    }
}
