package io.playqd.mini.controller.factories;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;

import io.playqd.mini.controller.item.FolderItemRow;
import io.playqd.mini.controller.item.LibraryItemRow;

public final class FileSizeTableCellFactory implements MiscValueTableCellFactory {

    @Override
    public TableCell<LibraryItemRow, String> call(TableColumn<LibraryItemRow, String> param) {

        return new TextFieldTableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null) {
                    var rowItem = getTableRow().getItem();
                    if (rowItem instanceof FolderItemRow folderItemRow) {
                        if (folderItemRow.getSource().itemType().isFile()) {
                            setText(folderItemRow.getSource().displaySize());
                        } else {
                            setText(null);
                        }
                    }
                }
            }
        };
    }
}
