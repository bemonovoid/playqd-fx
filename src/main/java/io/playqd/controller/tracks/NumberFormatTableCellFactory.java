package io.playqd.controller.tracks;

import io.playqd.utils.Numbers;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import java.util.function.Function;

class NumberFormatTableCellFactory implements Callback<TableColumn<TrackModel, Integer>, TableCell<TrackModel, Integer>> {

    private final Function<TrackModel, Integer> trackNumberValue;

    NumberFormatTableCellFactory(Function<TrackModel, Integer> trackNumberValue) {
        this.trackNumberValue = trackNumberValue;
    }

    @Override
    public TableCell<TrackModel, Integer> call(TableColumn<TrackModel, Integer> param) {
        return new TextFieldTableCell<>() {

            @Override
            public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !isEmpty()) {
                    var intValue = trackNumberValue.apply(getTableRow().getItem());
                    setText(Numbers.format(intValue));
                }
            }
        };
    }
}
