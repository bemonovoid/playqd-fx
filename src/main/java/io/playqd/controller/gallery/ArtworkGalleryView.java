package io.playqd.controller.gallery;

import io.playqd.client.Images;
import io.playqd.client.PlayqdApis;
import io.playqd.client.PlayqdClientProvider;
import io.playqd.controller.view.menuitem.ShowInFolderItems;
import io.playqd.data.AlbumFolderImageRef;
import io.playqd.data.Track;
import io.playqd.event.MouseEventHelper;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.WindowEvent;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ArtworkGalleryView extends BorderPane {

    private static final Logger LOG = LoggerFactory.getLogger(ArtworkGalleryView.class);

    private final Track track;
    private final Image artworkImage;
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
        var image = Images.album(track.id());
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
            var imageRefs = PlayqdClientProvider.get().watchFolders().getAlbumFolderImages(track.id());
            images.get().addAll(imageRefs);
            for (int i = 0; i < images.get().size(); i++) {
                var image = (Image) null;
                if (i == 0 && images.get().get(i).id() == null) {
                    image = artworkImage;
                } else {
                    var imageUrl = PlayqdApis.albumFolderImage(track.id(), images.get().get(i).id());
                    image = new Image(imageUrl, 100, 100, true, true, true);
                }
                var imageView = new ImageView(image);

                imageView.setFocusTraversable(false);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setSmooth(true);
                imageView.setCache(true);
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
        mainImageView.setOnMouseClicked(e -> {
            if (MouseEventHelper.secondaryButtonSingleClicked(e)) {
                var contextMenu = new ContextMenu();
                var items = new ShowInFolderItems(() -> {
                    var userData = mainImageView.getUserData();
                    mainImageView.fireEvent(new WindowEvent(
                            mainImageView.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
                    if (userData == null) {
                        return Paths.get(track.fileAttributes().location());
                    } else {
                        return Paths.get(userData.toString());
                    }
                }).build();
                contextMenu.getItems().addAll(items);
                contextMenu.show(mainImageView, e.getScreenX(), e.getScreenY());
            }
        });
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
                var imageUrl = PlayqdApis.albumFolderImage(track.id(), imageRef.id());
                var image = new Image(
                        imageUrl,
                        centerPane.widthProperty().multiply(0.9).get(),
                        centerPane.heightProperty().multiply(0.9).get(),
                        true,
                        true,
                        true);
                mainImageView.setImage(image);
                mainImageView.setUserData(imageRef.location());
                imageFileName.setText(imageRef.filename() + " (" + FileUtils.byteCountToDisplaySize(imageRef.size()) + ")");
            }
        }
    }
}
