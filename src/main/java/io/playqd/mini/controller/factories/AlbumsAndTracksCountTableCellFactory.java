package io.playqd.mini.controller.factories;

import io.playqd.client.Images;
import io.playqd.data.Tuple;
import io.playqd.mini.controller.item.LibraryItemRow;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.function.Function;

public final class AlbumsAndTracksCountTableCellFactory implements MiscValueTableCellFactory {

    private final Function<LibraryItemRow, Tuple<Integer, Integer>> countsProducer;

    public AlbumsAndTracksCountTableCellFactory(Function<LibraryItemRow, Tuple<Integer, Integer>> countsProducer) {
        this.countsProducer = countsProducer;
    }

    @Override
    public TableCell<LibraryItemRow, String> call(TableColumn<LibraryItemRow, String> param) {

        return new TextFieldTableCell<>() {

            private final HBox container = new HBox();
            private final Label albumsCountLabel = new Label();
            private final Label tracksCountLabel = new Label();

            {
                var albumsHbox = new HBox();
                var tracksHbox = new HBox();

                albumsHbox.setSpacing(1);
                albumsHbox.setAlignment(Pos.CENTER_LEFT);
                tracksHbox.setSpacing(1);
                tracksHbox.setAlignment(Pos.CENTER_LEFT);

                var tRegion = new Region();
                HBox.setHgrow(tRegion, Priority.ALWAYS);

                var albumImage = new ImageView(Images.defaultAlbum());
                var trackImage = new ImageView(Images.defaultAudioFile());

                albumsHbox.getChildren().addAll(albumImage, tRegion, albumsCountLabel);
                tracksHbox.getChildren().addAll(trackImage, tRegion, tracksCountLabel);

                container.setAlignment(Pos.CENTER_LEFT);
                container.getChildren().addAll(albumsHbox, new Label(", "), tracksHbox);
            }

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    return;
                }
                var counts = countsProducer.apply(getTableRow().getItem());
                albumsCountLabel.setText(String.valueOf(counts.left()));
                tracksCountLabel.setText(String.valueOf(counts.right()));
                setText(null);
                setGraphic(container);
            }
        };
    }
}
