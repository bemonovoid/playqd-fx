package io.playqd.controller.music;

import io.playqd.data.Track;
import io.playqd.event.MouseEventHelper;
import io.playqd.player.AlbumListViewActionListener;
import io.playqd.utils.ImageHelper;
import io.playqd.utils.PlayqdApis;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class AlbumsListViewCellFactory implements Callback<ListView<Track.Album>, ListCell<Track.Album>> {

    private static final Logger LOG = LoggerFactory.getLogger(AlbumsListViewCellFactory.class);

    private final AlbumListViewActionListener albumActionListener;

    AlbumsListViewCellFactory(AlbumListViewActionListener albumActionListener) {
        this.albumActionListener = albumActionListener;
    }

    static void main() {
        var uuid = UUID.nameUUIDFromBytes("Caspian".getBytes()).toString();
        var uuid2 = UUID.nameUUIDFromBytes("Anathema".getBytes()).toString();
        System.out.println(uuid);
    }

    @Override
    public ListCell<Track.Album> call(ListView<Track.Album> albumGridView) {

        return new ListCell<>() {

            @Override
            protected void updateItem(Track.Album album, boolean empty) {

                setOnMouseClicked(event -> {
                    if (MouseEventHelper.primaryButtonDoubleClicked(event)) {
                        albumActionListener.onAlbumDoubleClicked(album);
                    }
                });

                super.updateItem(album, empty);

                if (empty || album == null) {
                    setGraphic(null);
                } else {
                    var imageView = new ImageView();

                    ImageHelper.loadImageWithFallback(
                            imageView,
                            80,
                            80,
                            PlayqdApis.baseUrl() + "/artworks/albums/" + album.id(),
                            "/img/album-artwork-not-found.png");

                    var vBox = new VBox();
                    vBox.setAlignment(Pos.TOP_LEFT);

                    var albNameLabel = new Label(album.name());
                    albNameLabel.setTooltip(new Tooltip(album.name()));
                    albNameLabel.setStyle("-fx-font-size: 16px;");

                    var albDate = new Label(album.releaseDate());
                    albDate.setDisable(true);
                    albDate.setStyle("-fx-font-size: 10px;");
                    albDate.setOpacity(0.6);

                    var albGenre = new Label(album.genre());
                    albGenre.setDisable(true);
                    albGenre.setStyle("-fx-font-size: 10px;");
                    albGenre.setOpacity(0.6);

                    var pane = new Pane();
                    VBox.setVgrow(pane, Priority.ALWAYS);

                    vBox.getChildren().addAll(albNameLabel, pane, albDate, albGenre);

                    var hBox = new HBox();
                    hBox.setSpacing(10);
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    hBox.setPadding(new Insets(10, 0, 10, 0)); // creates spacing between TableView rows

                    hBox.getChildren().addAll(imageView, vBox);

                    setGraphic(hBox);
                }
            }
        };
    }
}
