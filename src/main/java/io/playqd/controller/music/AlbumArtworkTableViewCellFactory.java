package io.playqd.controller.music;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class AlbumArtworkTableViewCellFactory
        implements Callback<TableColumn<TrackTableRow, String>, TableCell<TrackTableRow, String>> {

    @Override
    public TableCell<TrackTableRow, String> call(TableColumn<TrackTableRow, String> albumStringTableColumn) {
        var tableCell = buildTableCell();
        tableCell.setFocusTraversable(false);
        tableCell.getStyleClass().add("no-highlight-cell");
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
                        var albumId = trackTableRow.track().album().id();
                        var artworkImageView =
                                new ImageView(new Image("http://gkos-srv:8017/api/v1/artworks/albums/" + albumId));
                        artworkImageView.setFitWidth(50);
                        artworkImageView.setFitHeight(50);

                        var hBox = new HBox(artworkImageView);

                        hBox.setSpacing(5);
                        hBox.setAlignment(Pos.CENTER_LEFT);

                        setGraphic(hBox);
                    }
                }
            }
        };
    }
}
