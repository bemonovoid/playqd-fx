package io.playqd.mini.controller.factories;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.data.ItemType;
import io.playqd.data.WatchFolderItem;
import io.playqd.mini.controller.item.FolderItemRow;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.platform.PlatformApi;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import org.kordamp.ikonli.javafx.FontIcon;

public final class WatchFolderItemImageTableCellFactory extends ImageTableCellFactory {

    @Override
    public TableCell<LibraryItemRow, Long> call(TableColumn<LibraryItemRow, Long> param) {

        return new TextFieldTableCell<>() {

            @Override
            public void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !isEmpty()) {
                    setText(null);
                    if (getTableRow().getItem() instanceof FolderItemRow folderItemRow) {
                        var wfi = folderItemRow.getSource();
                        if (ItemType.FOLDER == wfi.itemType()) {
                            setGraphic(new FontIcon("far-folder"));
                        } else {
                            setIcon(wfi);
                        }
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
                    setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE_ALT));
                }
            }
        };
    }
}
