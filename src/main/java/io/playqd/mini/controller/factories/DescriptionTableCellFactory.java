package io.playqd.mini.controller.factories;

import io.playqd.mini.controller.item.LibraryItemRow;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public interface DescriptionTableCellFactory extends
        Callback<TableColumn<LibraryItemRow, String>, TableCell<LibraryItemRow, String>> {
}
