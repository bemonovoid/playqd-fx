package io.playqd.controller.music;

import io.playqd.data.Album;
import io.playqd.event.MouseEventHelper;
import io.playqd.player.AlbumGridViewActionListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlbumsGridViewCellFactory implements Callback<GridView<Album>, GridCell<Album>> {

    private static final Logger LOG = LoggerFactory.getLogger(AlbumsGridViewCellFactory.class);

    private final AlbumGridViewActionListener albumActionListener;

    AlbumsGridViewCellFactory(AlbumGridViewActionListener albumActionListener) {
        this.albumActionListener = albumActionListener;
    }

    @Override
    public GridCell<Album> call(GridView<Album> albumGridView) {

        return new GridCell<>() {

            @Override
            protected void updateItem(Album album, boolean empty) {

                var imageView = new ImageView();

                imageView.setOnMouseClicked(mouseEvent -> {
                    if (MouseEventHelper.primaryButtonSingleClicked(mouseEvent)) {
                        albumActionListener.onAlbumImageClicked(album);
                    } else if (MouseEventHelper.primaryButtonDoubleClicked(mouseEvent)) {
                        albumActionListener.onAlbumImageDoubleClicked(album);
                    }
                });

                super.updateItem(album, empty);

                if (empty || album == null) {
                    setGraphic(null);
                } else {
                    var url = "http://gkos-srv:8017/api/v1/artworks/albums/" + album.id();
                    var img = new Image(url, 80, 80, true, true, false);
                    imageView.setImage(img);

                    var vBox = new VBox();
                    vBox.setAlignment(Pos.CENTER);

                    var albNameLabel = new Label(album.name());
                    albNameLabel.setTooltip(new Tooltip(album.name()));

                    var albDate = album.releaseDate();
                    var albGenreDate = album.genre();
                    vBox.getChildren().addAll(
                            imageView, albNameLabel, new Label(albDate), new Label(albGenreDate));

                    setGraphic(vBox);
                }
            }
        };
    }
}
