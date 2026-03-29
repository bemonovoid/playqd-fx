package io.playqd.controller.collections;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.data.MediaCollection;
import io.playqd.service.MusicLibrary;
import io.playqd.utils.Numbers;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import javafx.util.Callback;

class CollectionsListViewCellFactory
        implements Callback<ListView<MediaCollection>, ListCell<MediaCollection>> {

    @Override
    public ListCell<MediaCollection> call(ListView<MediaCollection> collectionsListView) {

        return new ListCell<>() {

            @Override
            protected void updateItem(MediaCollection listItem, boolean empty) {

                super.updateItem(listItem, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (listItem != null) {

                    setText(null);
                    setContextMenu(buildCellContextMenu(getItem(), getListView()));

                    var hBox = new HBox();

                    var image = new ImageView();
                    image.setFitHeight(25);
                    image.setFitWidth(25);

                    var countText = listItem.items().size() == 1 ? " item" : " items";
                    var countTextLabel = new Label(Numbers.format(listItem.items().size()) + countText);
                    countTextLabel.setDisable(true);
                    countTextLabel.setStyle("-fx-font-size: 10px;");

                    var listItemLabel = new Label(listItem.name());
                    listItemLabel.setStyle("-fx-font-size: 14px;");

                    var vBox = new VBox();
                    vBox.getChildren().addAll(listItemLabel, countTextLabel);

                    hBox.getChildren().addAll(image, vBox);

                    setGraphic(hBox);

                    // Removes horizontal scroll.
                    // The horizontal scrollbar appears because the cells are wider than the list.
                    // To fix the root cause, bind the preferred width of the cells to the width of the ListView
                    prefWidthProperty().bind(getListView().widthProperty().subtract(20));

                } else {
                    setText("null");
                    setGraphic(null);
                }
            }
        };
    }

    private ContextMenu buildCellContextMenu(MediaCollection mediaCollection,
                                             ListView<MediaCollection> listView) {
        var contextMenu = new ContextMenu();
        contextMenu.getItems().add(renameMenuItem(mediaCollection, listView));
        contextMenu.getItems().add(new SeparatorMenuItem());
        contextMenu.getItems().add(deleteMenuItem(mediaCollection, listView));
        return contextMenu;
    }

    private MenuItem renameMenuItem(MediaCollection mediaCollection, ListView<MediaCollection> listView) {
        var icon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL, "12");
        icon.setStyle("-fx-fill: #ff9d5f");
        var menuItem = new MenuItem("Rename", icon);
        menuItem.setOnAction(_ -> {
            new CollectionDialog(mediaCollection.name()).showAndWait().ifPresent(newName -> {
                var updated = MusicLibrary.updateCollection(mediaCollection.id(), newName);
                listView.getSelectionModel().select(updated);
            });
        });
        return menuItem;
    }

    private MenuItem deleteMenuItem(MediaCollection mediaCollection, ListView<MediaCollection> listView) {
        var icon = new FontAwesomeIconView(FontAwesomeIcon.REMOVE, "12");
        icon.setStyle("-fx-fill: #ff0000;");
        var menuItem = new MenuItem("Delete", icon);
        menuItem.setOnAction(_ -> {
            var alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete collection");
            alert.initStyle(StageStyle.UTILITY);
            alert.setHeaderText(null);
            alert.setContentText(String.format("Delete '%s' collection with %s item(s)?",
                    mediaCollection.name(), mediaCollection.items().size()));
            var result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                MusicLibrary.deleteCollection(mediaCollection.id());
//                listView.refresh();
            }
        });
        return menuItem;
    }
}
