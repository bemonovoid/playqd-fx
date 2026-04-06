package io.playqd.controller.folders;

import io.playqd.client.PlayqdClientProvider;
import io.playqd.controller.view.ApplicationViews;
import io.playqd.controller.view.ObservableProperties;
import io.playqd.data.ItemType;
import io.playqd.data.WatchFolderItem;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class FoldersViewController {

    private static final Logger LOG = LoggerFactory.getLogger(FoldersViewController.class);

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
        setViewRequestListeners();
    }

    private void setViewRequestListeners() {
        ObservableProperties.getAppViewRequestProperty().addListener((_, _, newView) -> {
            if (newView != null && ApplicationViews.FOLDERS == newView.view()) {
                var viewReq = newView.foldersViewRequest();
                var targetLocation = viewReq.location();
                var parent = foldersTreeViewController.foldersTreeView.getRoot();
                LOG.info("Browsing to location: {}.", targetLocation);
                var found = findTarget(targetLocation, parent);
                if (found) {
                    folderItemsTableViewController.selectItem(targetLocation);
                }
            }
        });
    }

    private boolean findTarget(Path targetPath, TreeItem<WatchFolderItem> parent) {
        if (parent == null || parent.getChildren().isEmpty()) {
            return false;
        }
        for (var childItem : parent.getChildren()) {
            var wfi = childItem.getValue();
            if (wfi == null) {
                return false;
            }
            if (targetPath.startsWith(wfi.path())) {
                var targetFound = targetPath.equals(wfi.path()) || targetPath.getParent().equals(wfi.path());
                LOG.info("Target location exists: {}.", targetFound);
                if (targetFound) {
                    foldersTreeViewController.foldersTreeView.getSelectionModel().select(childItem);
                    var selectedIdx = foldersTreeViewController.foldersTreeView.getSelectionModel().getSelectedIndex();
                    foldersTreeViewController.foldersTreeView.scrollTo(selectedIdx);
                    return true;
                }

                if (childItem.getChildren().isEmpty()) {
                    var itemsFromServer = FoldersViewController.getChildrenFromServer(wfi.id(), ItemType.FOLDER);
                    childItem.getChildren().setAll(itemsFromServer);
                    childItem.setExpanded(true);
                }

                LOG.info("Found parent location: {}. Browsing next {} children items ...",
                        wfi.path(), childItem.getChildren().size());

                return findTarget(targetPath, childItem);
            }
        }
        return false;
    }

    static List<TreeItem<WatchFolderItem>> getChildrenFromServer(String parentId) {
        return getChildrenFromServer(parentId, null);
    }

    static List<TreeItem<WatchFolderItem>> getChildrenFromServer(String parentId, ItemType itemType) {
        return PlayqdClientProvider.get()
                .watchFolderChildrenItems(parentId, itemType).stream()
                .sorted(Comparator.comparing(WatchFolderItem::name))
                .map(TreeItem::new)
                .toList();
    }
}
