package io.playqd.controller.music;

import io.playqd.data.Artist;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class ArtistsListViewCellFactory implements Callback<ListView<Artist>, ListCell<Artist>> {


    @Override
    public ListCell<Artist> call(ListView<Artist> artistListView) {
        return new ListCell<>() {
            @Override
            protected void updateItem(Artist artist, boolean empty) {
                super.updateItem(artist, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (artist != null) {
                    setText(null);
                    var hBox = new HBox();

                    var image = new ImageView();
                    image.setFitHeight(25);
                    image.setFitWidth(25);

                    var vBox = new VBox();
                    vBox.getChildren().addAll(new Label(artist.name()), new Label(artist.albumsCount() + " albums"));

                    hBox.getChildren().addAll(image, vBox);


                    setGraphic(hBox);
                } else {
                    setText("null");
                    setGraphic(null);
                }
            }
        };
    }
}
