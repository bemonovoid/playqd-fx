package io.playqd.mini.controller.configurer;

import io.playqd.data.Track;
import io.playqd.mini.controller.ItemsTableColumnIds;
import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.NavigableItemsResolver;
import io.playqd.mini.controller.factories.DescriptionTableCellFactory;
import io.playqd.mini.controller.factories.HyperLinkTableCellFactory;
import io.playqd.mini.controller.factories.ImageTableCellFactory;
import io.playqd.mini.controller.factories.TrackImageTableCellFactory;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.TrackItemRow;
import io.playqd.mini.controller.item.contextmenu.ContextMenuItemsBuilder;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import io.playqd.player.PlayerTrackListManager;
import io.playqd.player.TrackListRequest;
import io.playqd.utils.TimeUtils;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

public sealed class TracksViewConfigurer extends DefaultItemsViewConfigurer permits
        ArtistTracksViewConfigurer,
        AlbumTracksViewConfigurer,
        QueuedTracksViewConfigurer,
        PlaylistTracksViewConfigurer {

    private final static Logger LOG = LoggerFactory.getLogger(TracksViewConfigurer.class);

    public TracksViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    public void configureColumns(TableView<LibraryItemRow> tableView) {
        super.configureColumns(tableView);
        tableView.getColumns()
                .forEach(col -> {
                    if (col.getId().equals(ItemsTableColumnIds.MISC_VALUE_COL)) {
                        col.setVisible(true);
                        @SuppressWarnings("unchecked")
                        var miscValueCol = (TableColumn<LibraryItemRow, String>) col;
                        miscValueCol.setCellValueFactory(c -> c.getValue().getMiscValue());
                    }
                });
    }

    @Override
    public void configureFooter(TableView<LibraryItemRow> tableView, Label footerLabel) {
        var items = tableView.getItems();
        if (items.isEmpty()) {
            footerLabel.setText("");
        } else {
            var totalSize = 0L;
            var totalDurationInSeconds = 0L;
            for (LibraryItemRow item : items) {
                var t = (Track) item.getSource();
                totalSize += t.fileAttributes().size();
                totalDurationInSeconds += t.length().seconds();
            }
            var text = String.format("%s file%s, %s, %s",
                    NumberFormat.getInstance().format(items.size()),
                    items.size() > 1 ? "s" : "",
                    FileUtils.byteCountToDisplaySize(totalSize),
                    TimeUtils.durationToTimeFormat(Duration.ofSeconds(totalDurationInSeconds)));

            footerLabel.setText(text);
        }
    }

    @Override
    public void onItemsOpen(List<LibraryItemRow> items) {
        if (items.isEmpty()) {
            return;
        }
        if (items.getFirst() instanceof TrackItemRow trackItemRow) {
            PlayerTrackListManager.enqueueAndPlay(new TrackListRequest(trackItemRow.getSource()));
        } else {
            LOG.error("Unexpected item type: {}. Expected type: {}", items.getFirst().getClass(),  TrackItemRow.class);
        }
    }

    @Override
    public List<MenuItem> configureContextMenuItems(List<LibraryItemRow> selectedItems) {
        var items = selectedItems.stream().map(i -> (Track) i.getSource()).toList();
        return ContextMenuItemsBuilder.newBuilder(controller)
                .playMenuItems(items)
                .separatorMenuItem()
                .playlistMenuItems(items)
                .collectionsMenuItems(selectedItems)
                .separatorMenuItem()
                .showInContextMenuItems(selectedItems)
                .build();
    }

    @Override
    protected ImageTableCellFactory geImageTableCellFactory() {
        return new TrackImageTableCellFactory();
    }

    @Override
    protected DescriptionTableCellFactory getDescriptionTableCellFactory() {
        return new HyperLinkTableCellFactory(NavigableItemsResolver::resolveArtistAlbums);
    }

    @Override
    protected void configureHeaderLeft(ItemsDescriptor itemsDescriptor, HBox headerLeft) {
        headerLeft.getChildren().add(new Label("Tracks:"));
    }

    @Override
    public Supplier<List<MenuItem>> configureViewOptionsMenuItems(TableView<LibraryItemRow> tableView) {
        return () -> {
            var trackContextOptions = new TrackContextViewOptions(tableView);
            return List.of(trackContextOptions.getFilterByMenu(), trackContextOptions.getSortByMenu());
        };
    }

 }
