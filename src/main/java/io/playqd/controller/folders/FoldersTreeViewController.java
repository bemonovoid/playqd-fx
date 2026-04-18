package io.playqd.controller.folders;

import io.playqd.client.PlayqdClientProvider;
import io.playqd.data.ItemType;
import io.playqd.data.WatchFolderItem;
import io.playqd.service.MusicLibrary;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class FoldersTreeViewController {

    private static final Logger LOG = LoggerFactory.getLogger(FoldersTreeViewController.class);

    @FXML
    TreeView<WatchFolderItem> foldersTreeView;

    @FXML
    private void initialize() {
        foldersTreeView.setCellFactory(new FolderTreeItemCellFactory());
        foldersTreeView.setRoot(buildRootItem());
        foldersTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        foldersTreeView.getSelectionModel().selectedItemProperty().addListener((_, _, selectedItem) -> {
            if (selectedItem != null && !selectedItem.isExpanded() && selectedItem.getValue().hasNonFolderChildren()) {
                selectedItem.setExpanded(true);
            }
        });
        MusicLibrary.libraryRefreshedEventProperty().addListener((_, _, _) -> {
            foldersTreeView.getRoot().getChildren().clear();
            foldersTreeView.getRoot().getChildren().addAll(convertWatchFoldersToItems());
        });
    }

    private static TreeItem<WatchFolderItem> buildRootItem() {
        var children = convertWatchFoldersToItems();
        var rootItem = new TreeItem<>(new WatchFolderItem(
                FolderConstants.ROOT_ITEM_ID,
                "",
                "Watch folders",
                "",
                (long) children.size(),
                "",
                null,
                "",
                Map.of(ItemType.FOLDER, (long) children.size()),
                null,
                null));

        rootItem.getChildren().addAll(children);

        return rootItem;
    }

    private static List<TreeItem<WatchFolderItem>> convertWatchFoldersToItems() {
        var watchFolders = PlayqdClientProvider.get().watchFolders().getAll();
        return watchFolders.stream()
                .map(wf -> PlayqdClientProvider.get().watchFolders().getItemById(wf.uuid()))
                .map(TreeItem::new)
                .toList();
    }

}
