package io.playqd.controller.view;

import io.playqd.utils.TimeUtils;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import java.time.Duration;

class TrackTimeTableCellFactory
        implements Callback<TableColumn<TrackTableRow, Integer>, TableCell<TrackTableRow, Integer>> {

    @Override
    public TableCell<TrackTableRow, Integer> call(TableColumn<TrackTableRow, Integer> param) {
        return new TextFieldTableCell<>() {

            @Override
            public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !isEmpty()) {
                    setText(TimeUtils.durationToTimeFormat(Duration.ofSeconds(item)));
                }
            }
        };
    }
}
