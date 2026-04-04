package io.playqd.controller.gallery;

import io.playqd.client.ArtworkImages;
import io.playqd.client.PlayqdApis;
import io.playqd.client.PlayqdClientProvider;
import io.playqd.data.AlbumFolderImageRef;
import io.playqd.data.Track;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ArtworkGalleryView extends BorderPane {

    private final Track track;
    private final AtomicInteger currentIndexRef = new AtomicInteger();
    private final AtomicReference<List<AlbumFolderImageRef>> images = new AtomicReference<>();

    @FXML
    private StackPane centerPane;

    @FXML
    private Label imageFileName;

    @FXML
    private ImageView mainImageView;

    @FXML
    private HBox thumbnailHBox;

    public ArtworkGalleryView(Track track) {
        this.track = track;
        var image = ArtworkImages.album(track.id());
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.ARTWORK_GALLERY_VIEW);
        resourceLoader.setRoot(this);
        resourceLoader.setController(this);
        FXMLLoaderUtils.loadObject(resourceLoader, ArtworkGalleryView.class);
        mainImageView.setImage(image);
    }

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            var imageRefs = PlayqdClientProvider.get().getAlbumFolderImages(track.id());
            images.set(imageRefs);
            for (int i = 0; i < imageRefs.size(); i++) {
                var imageUrl = PlayqdApis.albumFolderImage(track.id(), imageRefs.get(i).id());
                var imageView = new ImageView(new Image(imageUrl, true));
                imageView.setFitWidth(120);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);
                var hyperlink = new Hyperlink();

                hyperlink.setUserData(i);
                hyperlink.setGraphic(imageView);
                hyperlink.setOnMouseClicked(_ -> {
                    currentIndexRef.set((int) hyperlink.getUserData());
                    updateMainImage();
                });

                thumbnailHBox.getChildren().add(hyperlink);
            }
        });
        mainImageView.fitWidthProperty().bind(centerPane.widthProperty().multiply(0.9));
        mainImageView.fitHeightProperty().bind(centerPane.heightProperty().multiply(0.9));
    }

    @FXML
    private void navigateLeft() {
        navigate(-1);
    }

    @FXML
    private void navigateRight() {
        navigate(1);
    }

    private void navigate(int direction) {
        var currentIndex = this.currentIndexRef.get();
        currentIndex += direction;
        if (currentIndex < 0) {
            currentIndex = images.get().size() - 1;
        }
        if (currentIndex >= images.get().size()) {
            currentIndex = 0;
        }
        this.currentIndexRef.set(currentIndex);
        updateMainImage();
    }

    private void updateMainImage() {
        if (!images.get().isEmpty()) {
            var imageRef = images.get().get(currentIndexRef.get());
            var imageUrl = PlayqdApis.albumFolderImage(track.id(), imageRef.id());
            imageFileName.setText(imageRef.filename());
            mainImageView.setImage(new Image(imageUrl, true));
        }
    }
}
