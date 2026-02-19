package io.playqd.controller.music;

import io.playqd.data.Album;
import io.playqd.event.MouseEventHelper;
import io.playqd.utils.FakeIds;
import io.playqd.utils.ImageHelper;
import io.playqd.utils.PlayqdApis;
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

public class AlbumsListViewCellFactory implements Callback<ListView<Album>, ListCell<Album>> {

    private static final Logger LOG = LoggerFactory.getLogger(AlbumsListViewCellFactory.class);

    private final AlbumsListViewCellFactoryListener cellFactoryListener;

    AlbumsListViewCellFactory(AlbumsListViewCellFactoryListener cellFactoryListener) {
        this.cellFactoryListener = cellFactoryListener;
    }

    @Override
    public ListCell<Album> call(ListView<Album> albumGridView) {

        return new ListCell<>() {

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

                    if (album.name().equals(FakeIds.ALL_ARTIST_ALBUMS)) {

                        var artistNameLabel = new HyperlinkLabel("[" + album.artistName() + "]");
                        artistNameLabel.setStyle("-fx-font-size: 13px;-fx-font-weight: bold");
                        artistNameLabel.setOnAction(_ -> cellFactoryListener.onAllArtistAlbumsClicked(album.id()));
                        var vBoxContainer = new VBox();

                        vBoxContainer.setSpacing(3);
                        vBoxContainer.getChildren().addAll(artistNameLabel, new Separator(Orientation.HORIZONTAL));
                        setGraphic(vBoxContainer);
                        return;
                    }

                    var imageView = new ImageView();

                    ImageHelper.loadImageWithFallback(
                            imageView,
                            80,
                            80,
                            PlayqdApis.albumArtwork(album.id()),
                            "/img/no-album-art-2.png");

                    var vBox = new VBox();
                    vBox.setAlignment(Pos.TOP_LEFT);

                    var albNameLabel = new Label(album.name());
                    albNameLabel.setTooltip(new Tooltip(album.name()));
                    albNameLabel.setStyle("-fx-font-size: 15px;-fx-font-weight: medium");

                    var albTracksCountLabel = new Label(
                            album.tracksCount() + (album.tracksCount() > 1 ? " tracks" : " track"));
                    albTracksCountLabel.setDisable(true);
                    albTracksCountLabel.setStyle("-fx-font-size: 10px;");
                    albTracksCountLabel.setOpacity(0.6);

                    var albLengthLabel = new Label(
                            TimeUtils.durationToTimeFormat(Duration.ofSeconds(album.lengthInSeconds())));
                    albLengthLabel.setDisable(true);
                    albLengthLabel.setStyle("-fx-font-size: 10px;");
                    albLengthLabel.setOpacity(0.6);

                    var albDate = new Label(album.releaseDate());
                    albDate.setDisable(true);
                    albDate.setStyle("-fx-font-size: 10px;");
                    albDate.setOpacity(0.6);

                    var albGenre = new Label(album.genre());
                    albGenre.setDisable(true);
                    albGenre.setStyle("-fx-font-size: 10px;");
                    albGenre.setOpacity(0.6);

//                    var pane = new Pane();
//                    VBox.setVgrow(pane, Priority.ALWAYS);

                    vBox.getChildren().addAll(albNameLabel, albTracksCountLabel, albLengthLabel, albDate, albGenre);

                    var hBox = new HBox();
                    hBox.setSpacing(10);
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    hBox.setPadding(new Insets(10, 0, 10, 0)); // creates spacing between ListView rows

                    hBox.getChildren().addAll(imageView, vBox);

                    setGraphic(hBox);
                }
            }
        };
    }

}
