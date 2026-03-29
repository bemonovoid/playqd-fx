package io.playqd.controller.library;

import io.playqd.data.Artist;
import io.playqd.client.ArtworkImages;
import javafx.geometry.Pos;
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

            private final HBox container = new HBox();
            private final Label artistNameLabel = new Label();
            private final Label itemsCountLabel = new Label();
            private final VBox artistInfoContainer = new VBox();
            private final ImageView artistImageView = new ImageView();

            {
                container.setSpacing(10);
                container.setAlignment(Pos.CENTER_LEFT);
                artistNameLabel.setStyle("-fx-font-size: 14px;");
                itemsCountLabel.setDisable(true);
                itemsCountLabel.setStyle("-fx-font-size: 10px;");
                artistInfoContainer.setSpacing(3);
                artistInfoContainer.getChildren().addAll(artistNameLabel, itemsCountLabel);
                container.getChildren().addAll(artistImageView, artistInfoContainer);
            }

            @Override
            protected void updateItem(Artist item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    if (item.id() > 0) {
                        var image = ArtworkImages.artist(item.id(), 35);
                        if (image != null) {
                            artistImageView.setImage(image);
                            image.errorProperty().addListener((_, _, hasError) -> {
                                if (hasError) {
                                    var defaultImage = ArtworkImages.defaultArtist(35);
                                    ArtworkImages.setArtist(item.id(), defaultImage, 35);
                                    artistImageView.setImage(defaultImage);
                                }
                            });
                        } else {
                            artistImageView.setImage(ArtworkImages.defaultArtist(35));
                        }
                    } else {
                        artistImageView.setImage(ArtworkImages.allArtistsImage());
                    }

                    var countText = item.albumsCount() > 1 ? " albums" : " album";
                    artistNameLabel.setText(item.name());
                    itemsCountLabel.setText(item.albumsCount() + countText);

                    setGraphic(container);

                    // Removes horizontal scroll.
                    // The horizontal scrollbar appears because the cells are wider than the list.
                    // To fix the root cause, bind the preferred width of the cells to the width of the ListView
                    prefWidthProperty().bind(getListView().widthProperty().subtract(20));

                }
            }
        };
    }
}
