package io.playqd.controller.trackexplorer;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.utils.Numbers;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.function.Consumer;

public class TracksExplorerListViewCellFactory implements Callback<ListView<ListItem>, ListCell<ListItem>> {

    @Override
    public ListCell<ListItem> call(ListView<ListItem> artistListView) {
        return new ListCell<>() {

            private final String iconSize = "14";

            private final HBox container = new HBox();
            private final VBox listItemContainer = new VBox();
            private final HBox titleContainer = new HBox();
            private final Label title = new Label();
            private final Label countTextLabel = new Label();

            {
                container.setAlignment(Pos.CENTER_LEFT);
                title.setStyle("-fx-font-size: 16px;");
                countTextLabel.setDisable(true);
                countTextLabel.setStyle("-fx-font-size: 11px;");
                countTextLabel.setPadding(new Insets(0, 0, 0, 25));

                titleContainer.setAlignment(Pos.CENTER_LEFT);
                titleContainer.setSpacing(10);
                titleContainer.getChildren().addAll(new Group(), title);

                listItemContainer.getChildren().addAll(titleContainer, countTextLabel);
                container.getChildren().addAll(new Group(), listItemContainer);
            }

            @Override
            protected void updateItem(ListItem listItem, boolean empty) {
                super.updateItem(listItem, empty);
                if (empty || listItem == null) {
                    setGraphic(null);
                } else {

                    var newCountConsumer = (Consumer<Integer>) count -> {
                        var countText = count == 1 ? " track" : " tracks";
                        countTextLabel.setText(Numbers.format(count) + countText);
                    };
                    newCountConsumer.accept(listItem.countProperty().intValue());
                    listItem.countProperty().addListener((_, _, newValue) -> {
                        if (newValue != null) {
                            newCountConsumer.accept(newValue.intValue());
                        }
                    });

                    title.setText(listItem.title());

                    switch (listItem.id()) {
                        case ALL -> titleContainer.getChildren().set(0,
                                new FontAwesomeIconView(FontAwesomeIcon.FILE_AUDIO_ALT, iconSize));
                        case LIKES -> titleContainer.getChildren().set(0,
                                new FontAwesomeIconView(FontAwesomeIcon.THUMBS_ALT_UP, iconSize));
                        case DISLIKES -> titleContainer.getChildren().set(0,
                                new FontAwesomeIconView(FontAwesomeIcon.THUMBS_ALT_DOWN, iconSize));
                        case PLAYED -> titleContainer.getChildren().set(0,
                                new FontAwesomeIconView(FontAwesomeIcon.PLAY, iconSize));
                        case CUE -> titleContainer.getChildren().set(0,
                                new FontAwesomeIconView(FontAwesomeIcon.FILE_TEXT_ALT, iconSize));
                    }

                    setGraphic(container);

                    // Removes horizontal scroll.
                    // The horizontal scrollbar appears because the cells are wider than the list.
                    // To fix the root cause, bind the preferred width of the cells to the width of the ListView
                    prefWidthProperty().bind(getListView().widthProperty().subtract(20));

                }
            }
        };
    }
}
