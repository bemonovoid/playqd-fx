package io.playqd.controller.view;

import io.playqd.utils.DateUtils;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import java.time.LocalDateTime;

class TrackDateTypeTableCellFactory
        implements Callback<TableColumn<TrackTableRow, LocalDateTime>, TableCell<TrackTableRow, LocalDateTime>> {

    @Override
    public TableCell<TrackTableRow, LocalDateTime> call(TableColumn<TrackTableRow, LocalDateTime> param) {
        return new TextFieldTableCell<>() {

            @Override
            public void updateItem(LocalDateTime dateItem, boolean empty) {
                super.updateItem(dateItem, empty);
                if (dateItem != null && !isEmpty()) {
                    setText(DateUtils.ldFormatted(dateItem.toLocalDate()));
                }
            }
        };
    }
}

