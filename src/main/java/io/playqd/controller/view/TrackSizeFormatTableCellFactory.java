package io.playqd.controller.view;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

class TrackSizeFormatTableCellFactory
        implements Callback<TableColumn<TrackTableRow, Long>, TableCell<TrackTableRow, Long>> {

    @Override
    public TableCell<TrackTableRow, Long> call(TableColumn<TrackTableRow, Long> col) {
        return new TextFieldTableCell<>() {

            @Override
            public void updateItem(Long size, boolean empty) {
                super.updateItem(size, empty);
                if (size != null && !isEmpty()) {
                    if (size == 0) {
                        setText("0");
                        return;
                    }
                    var displaySize = getTableRow().getItem().track().fileAttributes().readableSize();
                    setText(displaySize);
                }
            }
        };
    }
}
