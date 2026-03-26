package io.playqd.controller.folders;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.data.WatchFolderItem;
import io.playqd.platform.PlatformApi;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

class FilenameTableViewColumnCellFactory implements
        Callback<TableColumn<WatchFolderItem, String>, TableCell<WatchFolderItem, String>> {

    @Override
    public TableCell<WatchFolderItem, String> call(TableColumn<WatchFolderItem, String> column) {

        return new TableCell<>() {

            @Override
            public void updateItem(String text, boolean empty) {
                super.updateItem(text, empty);

                if (text != null && !isEmpty()) {
                    var wfi = getTableRow().getItem();
                    setText(wfi.name());
                    setIcon(wfi);
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
                    setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE_ALT));
                }
            }
        };
    }
}
