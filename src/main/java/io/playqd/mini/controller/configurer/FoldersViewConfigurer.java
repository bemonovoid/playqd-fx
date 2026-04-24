package io.playqd.mini.controller.configurer;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.playqd.client.PlayqdApis;
import io.playqd.data.ItemType;
import io.playqd.data.WatchFolderItem;
import io.playqd.mini.controller.ItemsTableColumnIds;
import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.NavigableItemsResolver;
import io.playqd.mini.controller.factories.FileSizeTableCellFactory;
import io.playqd.mini.controller.factories.ImageTableCellFactory;
import io.playqd.mini.controller.factories.MiscValueTableCellFactory;
import io.playqd.mini.controller.factories.NameTableCellFactory;
import io.playqd.mini.controller.factories.WatchFolderItemImageTableCellFactory;
import io.playqd.mini.controller.item.ArtistItemRow;
import io.playqd.mini.controller.item.FolderItemRow;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import io.playqd.platform.PlatformApi;
import io.playqd.player.PlayerTrackListManager;
import io.playqd.player.TrackListRequest;
import io.playqd.service.MusicLibrary;

public final class FoldersViewConfigurer extends DefaultItemsViewConfigurer {

    private final static Logger LOG = LoggerFactory.getLogger(FoldersViewConfigurer.class);

    public FoldersViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    protected Set<String> getIncludedColumns() {
        return Set.of(ItemsTableColumnIds.MISC_VALUE_COL);
    }

    protected Map<String, String> getColumnNameOverrides() {
        return Map.of(ItemsTableColumnIds.MISC_VALUE_COL, "Size");
    }

    @Override
    public void onOpen(TableView<LibraryItemRow> tableView) {
        var items =  tableView.getSelectionModel().getSelectedItems();
        if (items.getFirst() instanceof FolderItemRow folderItemRow) {
            if (ItemType.FOLDER == folderItemRow.getSource().itemType()) {
                controller.showItems(NavigableItemsResolver.resolveWatchFolderItems(folderItemRow));
            } else {
                openItems(items);
            }
        } else {
            LOG.warn("Unexpected item type: {}. Expected type: {}", items.getFirst().getClass(), ArtistItemRow.class);
        }
    }

    @Override
    protected void configureHeaderLeft(ItemsDescriptor itemsDescriptor, HBox headerLeft) {
        headerLeft.getChildren().add(new Label("Folders:"));
    }

    @Override
    protected ImageTableCellFactory geImageTableCellFactory() {
        return new WatchFolderItemImageTableCellFactory();
    }

    @Override
    protected NameTableCellFactory getNameTableCellFactory() {
        return new NameTableCellFactory(this, libraryItemRow -> {
            if (libraryItemRow.getSource() instanceof WatchFolderItem wfi) {
                var childrenCount = wfi.totalChildItemsCount();
                if (wfi.children().isEmpty()) {
                    if (wfi.itemType().isFile()) {
                        return wfi.mimeType();
                    }
                    return "<empty>";
                }
                var foldersCount = wfi.childFoldersCount();
                var filesCount = childrenCount - foldersCount;
                var foldersText = foldersCount > 0 ? foldersCount + " folder" : "";
                if (!foldersText.isEmpty()) {
                    foldersText += foldersCount > 1 ? "s" : "";
                }
                var filesText = filesCount > 0 ? filesCount + " file" : "";
                if (!filesText.isEmpty()) {
                    filesText += filesCount > 1 ? "s" : "";
                }

                if (foldersText.isEmpty()) {
                    return filesText;
                }
                if (filesText.isEmpty()) {
                    return foldersText;
                }
                return foldersText + ", " + filesText;
            }
            return null;
        });
    }

    @Override
    protected MiscValueTableCellFactory getMiscValueTableCellFactory() {
        return new FileSizeTableCellFactory();
    }

    @Override
    public void configureFooter(TableView<LibraryItemRow> tableView, Label footerLabel) {
        super.configureFooter(tableView, footerLabel);
        var totalSizeInBytes = tableView.getItems().stream()
                .filter(item -> item instanceof FolderItemRow)
                .map(item -> (FolderItemRow) item)
                .filter(item -> item.getSource().itemType() != ItemType.FOLDER)
                .mapToLong(item -> item.getSource().size())
                .sum();
        if (totalSizeInBytes > 0) {
            footerLabel.setText(footerLabel.getText() + ", " + FileUtils.byteCountToDisplaySize(totalSizeInBytes));
        }
    }

    private void openItems(List<LibraryItemRow> items) {
        if (items.size() == 1) {
            var wfi = (WatchFolderItem) items.getFirst().getSource();
            var mimeType = wfi.mimeType().toLowerCase();
            if (!mimeType.startsWith("audio")) {
                var location = PlayqdApis.watchFolderItemBinary(wfi.id());
                PlatformApi.open(location);
                return;
            }
        }
        var selectedItemPaths = items.stream()
                .map(i -> (WatchFolderItem) i.getSource())
                .filter(wfi -> wfi.mimeType().toLowerCase().startsWith("audio"))
                .map(WatchFolderItem::path)
                .collect(Collectors.toSet());
        var tracks = MusicLibrary.getTracksByPaths(selectedItemPaths);
        PlayerTrackListManager.enqueue(new TrackListRequest(tracks));
    }
}
