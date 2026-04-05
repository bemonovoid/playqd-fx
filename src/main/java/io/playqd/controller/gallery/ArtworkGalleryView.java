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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.apache.commons.io.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ArtworkGalleryView extends BorderPane {

    private Image artworkImage;

    private final Track track;
    private final AtomicInteger currentIndexRef = new AtomicInteger();
    private final AtomicReference<List<AlbumFolderImageRef>> images = new AtomicReference<>(new ArrayList<>());

    @FXML
    private StackPane centerPane;

    @FXML
    private Label imageFileName;

    @FXML
    private ImageView mainImageView;

    @FXML
    private ScrollPane thumbnailScrollPane;

    @FXML
    private HBox thumbnailHBox;

    public ArtworkGalleryView(Track track) {
        this.track = track;
        var image = ArtworkImages.album(track.id());
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.ARTWORK_GALLERY_VIEW);
        resourceLoader.setRoot(this);
        resourceLoader.setController(this);
        FXMLLoaderUtils.loadObject(resourceLoader, ArtworkGalleryView.class);
        images.get().addFirst(new AlbumFolderImageRef(null, "Artwork", null, 0));
        artworkImage = image;
    }

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            var imageRefs = PlayqdClientProvider.get().getAlbumFolderImages(track.id());
            images.get().addAll(imageRefs);
            for (int i = 0; i < images.get().size(); i++) {
                var image = (Image) null;
                if (i == 0 && images.get().get(i).id() == null) {
                    image = artworkImage;
                } else {
                    var imageUrl = PlayqdApis.albumFolderImage(track.id(), images.get().get(i).id());
                    image = new Image(imageUrl, true);
                }
                var imageView = new ImageView(image);
                imageView.setFocusTraversable(false);
                imageView.setFitWidth(120);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);
                imageView.setUserData(i);

                var btn = new Button();
                btn.setGraphic(imageView);
                btn.setOnAction(_ -> {
                    currentIndexRef.set((int) imageView.getUserData());
                    updateMainImage();
                });

                thumbnailHBox.getChildren().add(btn);
            }
            if (!images.get().isEmpty()) {
                ((Button) thumbnailHBox.getChildren().getFirst()).fire();
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
        var btn = (Button) thumbnailHBox.getChildren().get(currentIndex);
        btn.fire();
        btn.requestFocus();
        btn.localToScene(btn.getBoundsInLocal()); // Ensure layout is synced
        thumbnailScrollPane.setHvalue(btn.getLayoutX() / thumbnailHBox.getWidth());
    }

    private void updateMainImage() {
        if (!images.get().isEmpty()) {
            var imageRef = images.get().get(currentIndexRef.get());
            if (imageRef.id() == null) {
                mainImageView.setImage(artworkImage);
                imageFileName.setText(imageRef.filename());
            } else {
                var btn = (Button) thumbnailHBox.getChildren().get(currentIndexRef.get());
                var btmImageView = (ImageView) btn.getGraphic();
//                var imageUrl = PlayqdApis.albumFolderImage(track.id(), imageRef.id());
                mainImageView.setImage(btmImageView.getImage());
                imageFileName.setText(imageRef.filename() + " (" + FileUtils.byteCountToDisplaySize(imageRef.size()) + ")");
            }
        }
    }
}
