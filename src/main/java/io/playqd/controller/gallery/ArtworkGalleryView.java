package io.playqd.controller.gallery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.playqd.client.Images;
import io.playqd.client.PlayqdApis;
import io.playqd.data.ItemType;
import io.playqd.data.Track;
import io.playqd.data.WatchFolderItem;
import io.playqd.event.MouseEventHelper;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import io.playqd.service.MusicLibrary;

public class ArtworkGalleryView extends BorderPane {

    private static final Logger LOG = LoggerFactory.getLogger(ArtworkGalleryView.class);

    private final Track track;
    private final Image artworkImage;
    private final AtomicInteger currentIndexWfi = new AtomicInteger();
    private final AtomicReference<List<WatchFolderItem>> images = new AtomicReference<>(new ArrayList<>());

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
        var image = Images.getImage(track, -1);
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.ARTWORK_GALLERY_VIEW);
        resourceLoader.setRoot(this);
        resourceLoader.setController(this);
        FXMLLoaderUtils.loadObject(resourceLoader, ArtworkGalleryView.class);
        images.get().addFirst(new WatchFolderItem(
                null,
                "",
                "Artwork",
                "",
                0L,
                "",
                null,
                "",
                Map.of(),
                null,
                null));
        artworkImage = image;
    }

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            var wfis =
                    MusicLibrary.getWatchFolderItemsByLocation(track.fileAttributes().location(), ItemType.IMAGE_FILE);
            images.get().addAll(wfis);
            for (int i = 0; i < images.get().size(); i++) {
                var image = (Image) null;
                if (i == 0 && images.get().get(i).id() == null) {
                    image = artworkImage;
                } else {
                    var imageUrl = PlayqdApis.watchFolderItemBinary(images.get().get(i).id());
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
                    currentIndexWfi.set((int) imageView.getUserData());
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
//                var items = new ShowInFolderItems(() -> {
//                    var userData = mainImageView.getUserData();
//                    mainImageView.fireEvent(new WindowEvent(
//                            mainImageView.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
//                    if (userData == null) {
//                        return Paths.get(track.fileAttributes().location());
//                    } else {
//                        return Paths.get(userData.toString());
//                    }
//                }).build();
//                contextMenu.getItems().addAll(items);
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
        var currentIndex = this.currentIndexWfi.get();
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
            var imageWfi = images.get().get(currentIndexWfi.get());
            if (imageWfi.id() == null) {
                mainImageView.setImage(artworkImage);
                imageFileName.setText(imageWfi.name());
            } else {
                var imageUrl = PlayqdApis.watchFolderItemBinary(imageWfi.id());
                var image = new Image(
                        imageUrl,
                        centerPane.widthProperty().multiply(0.9).get(),
                        centerPane.heightProperty().multiply(0.9).get(),
                        true,
                        true,
                        true);
                mainImageView.setImage(image);
                mainImageView.setUserData(imageWfi.location());
                imageFileName.setText(imageWfi.name() + " (" + FileUtils.byteCountToDisplaySize(imageWfi.size()) + ")");
            }
        }
    }
}
