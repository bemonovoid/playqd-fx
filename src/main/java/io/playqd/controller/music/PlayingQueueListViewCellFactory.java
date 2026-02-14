package io.playqd.controller.music;

import io.playqd.event.MouseEventHelper;
import io.playqd.player.PlayerEngine;
import io.playqd.player.QueuedTrack;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayingQueueListViewCellFactory implements Callback<ListView<QueuedTrack>, ListCell<QueuedTrack>> {

    private static final Logger LOG = LoggerFactory.getLogger(PlayingQueueListViewCellFactory.class);

    @Override
    public ListCell<QueuedTrack> call(ListView<QueuedTrack> queuedTrackGridView) {

        return new ListCell<>() {

            @Override
            protected void updateItem(QueuedTrack queuedTrack, boolean empty) {

                super.updateItem(queuedTrack, empty);

                if (empty || queuedTrack == null) {
                    setText(null);
                } else {

                    var trackTitle = new Label(queuedTrack.track().title());

                    var artistName = new Label(queuedTrack.track().artist().name());
                    artistName.setDisable(true);
                    artistName.setStyle("-fx-font-size: 10px;");
                    artistName.setOpacity(0.6);

                    var pane = new Pane();
                    HBox.setHgrow(pane, Priority.ALWAYS);

                    var trackTime = new Label(queuedTrack.track().length().readable());

                    var vBox = new VBox();
                    vBox.setSpacing(3);
                    vBox.getChildren().addAll(trackTitle, artistName, trackTime);

                    var hBox = new HBox();
                    hBox.setSpacing(10);
                    hBox.setAlignment(Pos.CENTER_LEFT);

                    hBox.getChildren().addAll(vBox, pane, trackTime);

                    setOnMouseClicked(mouseEvent -> {
                        if (MouseEventHelper.primaryButtonDoubleClicked(mouseEvent)) {
                            PlayerEngine.playFromQueue(getIndex());
                        }
                    });

                    setGraphic(hBox);
                }
            }

            private void updateTime(QueuedTrack queuedTrack) {

            }
        };
    }
}
