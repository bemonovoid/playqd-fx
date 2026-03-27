package io.playqd.controller.folders;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.client.PlayqdClientProvider;
import io.playqd.data.ItemType;
import io.playqd.data.WatchFolderItem;
import io.playqd.event.MouseEventHelper;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

import java.util.Comparator;

class FolderTreeItemCellFactory implements Callback<TreeView<WatchFolderItem>, TreeCell<WatchFolderItem>> {

    @Override
    public TreeCell<WatchFolderItem> call(TreeView<WatchFolderItem> foldersTreeView) {

        return new TreeCell<>() {

            @Override
            protected void updateItem(WatchFolderItem item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    if (!FolderConstants.ROOT_ITEM_ID.equals(item.id())) {
//                        setContextMenu(buildCellContextMenu(item), () -> getTreeView().getSelectionModel().getSelectedItem().getValue());
                    }
                    var hBox = new HBox();
                    hBox.setSpacing(5);

                    var icon = getTreeItem().isExpanded() ? FontAwesomeIcon.FOLDER_OPEN_ALT : FontAwesomeIcon.FOLDER_ALT;
                    var iconView = new FontAwesomeIconView(icon, "18px");
                    iconView.setTextAlignment(TextAlignment.CENTER);
                    if (item.childFoldersCount() == 0) {
                        iconView.setStyle("-fx-fill: #ff6703;-fx-font-size: 18px");
                    }

                    var countText = item.totalChildItemsCount() > 1 ? " items" : " item";
                    var countTextLabel = new Label(item.totalChildItemsCount() + countText);
                    countTextLabel.setDisable(true);
                    countTextLabel.setStyle("-fx-font-size: 10px;");

                    var artistNameLabel = new Label(item.name());
                    artistNameLabel.setStyle("-fx-font-size: 14px;");

                    var vBox = new VBox();
                    vBox.getChildren().addAll(artistNameLabel, countTextLabel);

                    hBox.getChildren().addAll(iconView, vBox);

                    setGraphic(hBox);

                    // Removes horizontal scroll.
                    // The horizontal scrollbar appears because the cells are wider than the list.
                    // To fix the root cause, bind the preferred width of the cells to the width of the ListView
                    prefWidthProperty().bind(getTreeView().widthProperty().subtract(20));

                    setOnMouseClicked(mouseEvent -> {
                        if (MouseEventHelper.primaryButtonDoubleClicked(mouseEvent)) {
                            if (getTreeItem().getChildren().isEmpty()) {
                                var itemsFromServer = PlayqdClientProvider.get()
                                        .watchFolderChildrenItems(item.id(), ItemType.FOLDER).stream()
                                        .sorted(Comparator.comparing(WatchFolderItem::name))
                                        .map(TreeItem::new)
                                        .toList();
                                getTreeItem().getChildren().setAll(itemsFromServer);
                                getTreeItem().setExpanded(true);
                            }
                        }
                    });
                }
            }
        };
    }

    private ContextMenu buildCellContextMenu(WatchFolderItem watchFolderItem) {
        var contextMenu = new ContextMenu();
        var play = new MenuItem("Play", new FontAwesomeIconView(FontAwesomeIcon.PLAY));
        play.setOnAction(_ -> {
//            var tracks = MusicLibrary.getTracksByPaths(selectedItemPaths);
//            PlayerTrackListManager.enqueue(new TrackListRequest(tracks, 0));
        });

        var queueNext = new MenuItem("Queue next");
        var queueLast = new MenuItem("Queue last");
        contextMenu.getItems().addAll(play, queueNext, queueLast, new SeparatorMenuItem());
        return contextMenu;
    }
}
