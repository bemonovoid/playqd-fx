package io.playqd.controller.music;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class AlbumInfoTableViewCellFactory

        implements Callback<TableColumn<TrackTableRow, String>, TableCell<TrackTableRow, String>> {

    @Override
    public TableCell<TrackTableRow, String> call(TableColumn<TrackTableRow, String> tableColumn) {
        var tableCell = buildTableCell();
        tableCell.setFocusTraversable(false);

        tableCell.getStyleClass().add("no-highlight-cell");
//        if (tableCell.getTableRow() != null && !tableCell.getTableRow().isSelected()) {
//        }
        return tableCell;
    }

    private static TableCell<TrackTableRow, String> buildTableCell() {
        return new TextFieldTableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !isEmpty()) {
                    setText(null);

                    var trackTableRow = getTableRow().getItem();

                    if (trackTableRow.albumHeader()) {
                        var albName = trackTableRow.track().albumName();
                        var albDate = trackTableRow.track().releaseDate();
                        var albGenreDate = trackTableRow.track().genre();

                        var albumInfoVBox = new VBox(new Label(albName), new Label(albDate), new Label(albGenreDate));
                        albumInfoVBox.setSpacing(2);
                        albumInfoVBox.setAlignment(Pos.CENTER_LEFT);

                        var hBox = new HBox(albumInfoVBox);

                        hBox.setSpacing(5);
                        hBox.setAlignment(Pos.CENTER_LEFT);

                        setGraphic(hBox);
                    }
                }
            }
        };
    }
}
