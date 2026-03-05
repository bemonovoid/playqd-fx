package io.playqd.controller.view;

import io.playqd.data.Track;
import io.playqd.utils.Numbers;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import java.util.function.Function;

class NumberFormatTableCellFactory implements Callback<TableColumn<Track, Integer>, TableCell<Track, Integer>> {

    private final Function<Track, Integer> trackNumberValue;

    NumberFormatTableCellFactory(Function<Track, Integer> trackNumberValue) {
        this.trackNumberValue = trackNumberValue;
    }

    @Override
    public TableCell<Track, Integer> call(TableColumn<Track, Integer> param) {
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
