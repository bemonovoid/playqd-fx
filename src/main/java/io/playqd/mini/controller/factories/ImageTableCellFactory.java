package io.playqd.mini.controller.factories;

import io.playqd.mini.controller.item.LibraryItemRow;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public abstract class ImageTableCellFactory implements
        Callback<TableColumn<LibraryItemRow, Long>, TableCell<LibraryItemRow, Long>> {

}
