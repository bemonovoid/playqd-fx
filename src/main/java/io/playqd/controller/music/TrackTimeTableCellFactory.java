package io.playqd.controller.music;

import io.playqd.data.Track;
import io.playqd.utils.TimeUtils;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import java.time.Duration;

public class TrackTimeTableCellFactory implements
        Callback<TableColumn<Track, Integer>, TableCell<Track, Integer>> {

    @Override
    public TableCell<Track, Integer> call(TableColumn<Track, Integer> param) {
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
