package io.playqd.controller.tracks;

import io.playqd.utils.TimeUtils;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import java.time.Duration;

class TrackTimeTableCellFactory implements Callback<TableColumn<TrackModel, Integer>, TableCell<TrackModel, Integer>> {

    @Override
    public TableCell<TrackModel, Integer> call(TableColumn<TrackModel, Integer> param) {
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
