package io.playqd.player;

import io.playqd.data.Track;
import io.playqd.event.MouseEventHelper;
import io.playqd.utils.ArtworkImageSetter;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PlayerTrackListViewCellFactory implements Callback<ListView<Track>, ListCell<Track>> {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerTrackListViewCellFactory.class);

    @Override
    public ListCell<Track> call(ListView<Track> queuedTrackGridView) {

        return new ListCell<>() {

            @Override
            protected void updateItem(Track track, boolean empty) {

                super.updateItem(track, empty);

                if (empty || track == null) {
                    setText(null);
                } else {

                    var trackTitle = new Label(track.title());
//                    var trackTitleHbox = new HBox(trackTitle, createHiResBadge());

                    var artistName = new Label(track.artistName());
                    artistName.setDisable(true);
                    artistName.setStyle("-fx-font-size: 10px;");
                    artistName.setOpacity(0.6);

                    var artworkImage = new ImageView();
                    ArtworkImageSetter.set(track, artworkImage, 35);

                    var vBox = new VBox();
                    vBox.setSpacing(3);
                    vBox.getChildren().addAll(trackTitle, artistName);

                    var pane = new Pane();
                    HBox.setHgrow(pane, Priority.ALWAYS);

                    var trackTime = new Label(track.length().readable());

                    var hBox = new HBox();
                    hBox.setSpacing(10);
                    hBox.setAlignment(Pos.CENTER_LEFT);

                    hBox.getChildren().addAll(artworkImage, vBox, pane, trackTime);

                    setOnMouseClicked(mouseEvent -> {
                        if (MouseEventHelper.primaryButtonDoubleClicked(mouseEvent)) {
                            Player.play(track);
                        }
                    });

                    setGraphic(hBox);
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
