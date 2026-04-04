package io.playqd.controller.view;

import io.playqd.utils.Numbers;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import java.util.function.Function;

class NumberFormatTableCellFactory
        implements Callback<TableColumn<TrackTableRow, Integer>, TableCell<TrackTableRow, Integer>> {

    private final Function<TrackTableRow, Integer> trackNumberValue;

    NumberFormatTableCellFactory(Function<TrackTableRow, Integer> trackNumberValue) {
        this.trackNumberValue = trackNumberValue;
    }

    @Override
    public TableCell<TrackTableRow, Integer> call(TableColumn<TrackTableRow, Integer> param) {
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
