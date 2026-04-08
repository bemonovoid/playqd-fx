package io.playqd.controller.library;

import io.playqd.data.Album;
import io.playqd.event.MouseEventHelper;
import io.playqd.client.ArtworkImages;
import io.playqd.utils.FakeIds;
import io.playqd.utils.TimeUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.control.HyperlinkLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

class AlbumsListViewCellFactory implements Callback<ListView<Album>, ListCell<Album>> {

    private static final Logger LOG = LoggerFactory.getLogger(AlbumsListViewCellFactory.class);

    private final AlbumsListViewCellFactoryListener cellFactoryListener;

    AlbumsListViewCellFactory(AlbumsListViewCellFactoryListener cellFactoryListener) {
        this.cellFactoryListener = cellFactoryListener;
    }

    @Override
    public ListCell<Album> call(ListView<Album> albumGridView) {

        return new ListCell<>() {

            private static final int IMAGE_SIZE = 80;

            private final HBox container = new HBox();
            private final VBox albumInfoContainer = new VBox();
            private final ImageView imageView = new ImageView();
            private final Label nameLabel = new Label();
            private final Label countLabel = new Label();
            private final Label lengthLabel = new Label();
            private final Label dateLabel = new Label();
            private final Label genreLabel = new Label();

            private final VBox allAlbumsContainer = new VBox();
            private final HyperlinkLabel allAlbumsLabel = new HyperlinkLabel();

            {
                container.setSpacing(10);
                container.setAlignment(Pos.CENTER_LEFT);
                container.setPadding(new Insets(10, 0, 10, 0)); // creates spacing between ListView rows
                albumInfoContainer.setAlignment(Pos.TOP_LEFT);
                albumInfoContainer.getChildren().addAll(nameLabel, countLabel, lengthLabel, dateLabel, genreLabel);

                nameLabel.setStyle("-fx-font-size: 15px;-fx-font-weight: 500"); // FontWeight.MEDIUM
                countLabel.setDisable(true);
                countLabel.setStyle("-fx-font-size: 10px;");
                countLabel.setOpacity(0.6);
                lengthLabel.setDisable(true);
                lengthLabel.setStyle("-fx-font-size: 10px;");
                lengthLabel.setOpacity(0.6);
                dateLabel.setDisable(true);
                dateLabel.setStyle("-fx-font-size: 10px;");
                dateLabel.setOpacity(0.6);
                genreLabel.setDisable(true);
                genreLabel.setStyle("-fx-font-size: 10px;");
                genreLabel.setOpacity(0.6);

                container.getChildren().addAll(imageView, albumInfoContainer);

                allAlbumsLabel.setStyle("-fx-font-size: 13px;-fx-font-weight: bold");
                allAlbumsContainer.setSpacing(3);
                allAlbumsContainer.getChildren().addAll(allAlbumsLabel, new Separator(Orientation.HORIZONTAL));
            }

            @Override
            protected void updateItem(Album album, boolean empty) {
                super.updateItem(album, empty);

                if (empty || album == null) {
                    setGraphic(null);
                } else {

                    addEventFilter(MouseEvent.ANY, e -> {
                        if (e.getButton() == MouseButton.SECONDARY) {
                            e.consume();
                        }
                    });

                    setOnMouseClicked(event -> {
                        if (MouseEventHelper.primaryButtonDoubleClicked(event)) {
                            cellFactoryListener.onAlbumDoubleClicked(album);
                        } else if (MouseEventHelper.secondaryButtonSingleClicked(event)) {

                        }
                    });

                    if (FakeIds.ALL_ARTIST_ALBUMS_NAME.equals(album.name())) {
                        allAlbumsLabel.setText("[" + album.artistName() + "]");
                        allAlbumsLabel.setOnAction(_ -> cellFactoryListener.onAllArtistAlbumsClicked(album.id()));
                        setGraphic(allAlbumsContainer);
                        return;
                    }

                    var image = ArtworkImages.album(album.id(), IMAGE_SIZE);
                    if (image == null) {
                        imageView.setImage(ArtworkImages.defaultAlbum(IMAGE_SIZE));
                    } else {
                        imageView.setImage(image);
                        image.errorProperty().addListener((_, _, hasError) -> {
                            if (hasError) {
                                var defaultImage = ArtworkImages.defaultAlbum(IMAGE_SIZE);
                                ArtworkImages.setAlbum(album.id(), defaultImage, IMAGE_SIZE);
                                imageView.setImage(defaultImage);
                            }
                        });
                    }

                    nameLabel.setText(album.name());
                    nameLabel.setTooltip(new Tooltip(album.name()));
                    countLabel.setText(album.tracksCount() + (album.tracksCount() > 1 ? " tracks" : " track"));
                    lengthLabel.setText(TimeUtils.durationToTimeFormat(Duration.ofSeconds(album.lengthInSeconds())));
                    dateLabel.setText(album.releaseDate());
                    genreLabel.setText(album.genre());

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
