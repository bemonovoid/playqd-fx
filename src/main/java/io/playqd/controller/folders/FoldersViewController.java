package io.playqd.controller.folders;

import io.playqd.client.PlayqdClientProvider;
import io.playqd.data.ItemType;
import javafx.fxml.FXML;

public class FoldersViewController {

    @FXML
    private FoldersTreeViewController foldersTreeViewController;

    @FXML
    private FolderItemsTableViewController folderItemsTableViewController;

    @FXML
    private void initialize() {
        foldersTreeViewController.foldersTreeView.getSelectionModel().selectedItemProperty()
                .addListener((_, _, selectedTreeItem) -> {
                    if (selectedTreeItem != null) {
                        var item = selectedTreeItem.getValue();
                        if (item.hasNonFolderChildren()) {
                            var childrenItems = PlayqdClientProvider.get().watchFolderChildrenItems(item.id()).stream()
                                    .filter(wfi -> ItemType.FOLDER != wfi.itemType())
                                    .toList();
                            folderItemsTableViewController.setItems(childrenItems);
                        }
                    }
                });
    }
}
