package io.playqd.controller.folders;

import io.playqd.client.PlayqdClientProvider;
import io.playqd.data.ItemType;
import io.playqd.data.WatchFolderItem;
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
        var watchFolders = PlayqdClientProvider.get().getWatchFolders();
        var children = watchFolders.stream()
                .map(wf -> PlayqdClientProvider.get().getWatchFolderItem(wf.uuid()))
                .map(TreeItem::new)
                .toList();
        var rootItem = buildRootItem(children);
        rootItem.getChildren().addAll(children);
        foldersTreeView.setRoot(rootItem);
        foldersTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        foldersTreeView.getSelectionModel().selectedItemProperty().addListener((_, _, selectedItem) -> {
            if (selectedItem != null && !selectedItem.isExpanded() && selectedItem.getValue().hasNonFolderChildren()) {
                selectedItem.setExpanded(true);
            }
        });
    }

    private static TreeItem<WatchFolderItem> buildRootItem(List<TreeItem<WatchFolderItem>> children) {
        return new TreeItem<>(new WatchFolderItem(
                FolderConstants.ROOT_ITEM_ID,
                "",
                "Watch folders",
                "",
                (long) children.size(),
                "",
                null,
                Map.of(ItemType.FOLDER, (long) children.size()),
                null,
                null));
    }

}
