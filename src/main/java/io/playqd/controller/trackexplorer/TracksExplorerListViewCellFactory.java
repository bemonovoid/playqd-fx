package io.playqd.controller.trackexplorer;

import io.playqd.utils.Numbers;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.function.Consumer;

public class TracksExplorerListViewCellFactory implements Callback<ListView<ListItem>, ListCell<ListItem>> {

    @Override
    public ListCell<ListItem> call(ListView<ListItem> artistListView) {
        return new ListCell<>() {
            @Override
            protected void updateItem(ListItem listItem, boolean empty) {
                super.updateItem(listItem, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (listItem != null) {

                    setText(null);

                    var hBox = new HBox();

                    var image = new ImageView();
                    image.setFitHeight(25);
                    image.setFitWidth(25);

                    var countTextLabel = new Label();
                    countTextLabel.setDisable(true);
                    countTextLabel.setStyle("-fx-font-size: 10px;");
                    var newCountConsumer = (Consumer<Integer>) count-> {
                        var countText = count == 1 ? " track" : " tracks";
                        countTextLabel.setText(Numbers.format(count) + countText);
                    };
                    newCountConsumer.accept(listItem.countProperty().intValue());
                    listItem.countProperty().addListener((_, _, newValue) -> {
                        if (newValue != null) {
                            newCountConsumer.accept(newValue.intValue());
                        }
                    });

                    var listItemLabel = new Label(listItem.title());
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
}
