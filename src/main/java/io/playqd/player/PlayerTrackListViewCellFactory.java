package io.playqd.player;

import io.playqd.data.Track;
import io.playqd.event.MouseEventHelper;
import io.playqd.client.Images;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PlayerTrackListViewCellFactory implements Callback<ListView<Track>, ListCell<Track>> {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerTrackListViewCellFactory.class);

    @Override
    public ListCell<Track> call(ListView<Track> queuedTrackGridView) {

        return new ListCell<>() {

            private final HBox container = new HBox();
            private final ImageView imageView = new ImageView();
            private final Label trackTitleLabel = new Label();
            private final Label artistNameLabel = new Label();
            private final Label trackTimeLabel = new Label();

            {
                container.setSpacing(10);
                container.setAlignment(Pos.CENTER_LEFT);

                artistNameLabel.setDisable(true);
                artistNameLabel.setStyle("-fx-font-size: 10px;");
                artistNameLabel.setOpacity(0.6);

                var vBox = new VBox();
                vBox.setSpacing(3);
                vBox.getChildren().addAll(trackTitleLabel, artistNameLabel);
                var region = new Region();
                HBox.setHgrow(region, Priority.ALWAYS);

                container.getChildren().addAll(imageView, vBox, region, trackTimeLabel);
            }

            @Override
            protected void updateItem(Track track, boolean empty) {

                super.updateItem(track, empty);

                if (empty || track == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    var image = Images.album(track.id(), 25);
                    if (image == null) {
                        imageView.setImage(Images.defaultAlbum(25));
                    } else {
                        imageView.setImage(image);
                        image.errorProperty().addListener((_, _, hasError) -> {
                            if (hasError) {
                                var defaultImage = Images.defaultAlbum(25);
                                Images.setAlbum(track.id(), defaultImage, 25);
                                imageView.setImage(defaultImage);
                            }
                        });
                    }
                    trackTitleLabel.setText(track.name());
                    artistNameLabel.setText(track.artistName());
                    trackTimeLabel.setText(track.length().readable());
//                    var trackTitleHbox = new HBox(trackTitle, createHiResBadge());

                    setOnMouseClicked(mouseEvent -> {
                        if (MouseEventHelper.primaryButtonDoubleClicked(mouseEvent)) {
                            Player.play(track);
                        }
                    });

                    setGraphic(container);
                }
            }

            private void updateTime(QueuedTrack queuedTrack) {

            }

            private Label createHiResBadge() {
                var hiResBadge = new Label("HI-RES");

                // Styling the label to look like a badge
                hiResBadge.setStyle(
                        "-fx-background-color: #FFD700;" + // Gold background
                                "-fx-text-fill: black;" +
                                "-fx-font-size: 9px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-padding: 2 5 2 5;" +
                                "-fx-background-radius: 3;"
                );

//                hiResBadge.setVisible(false); // Hidden by default
                return hiResBadge;
            }
        };
    }
}
