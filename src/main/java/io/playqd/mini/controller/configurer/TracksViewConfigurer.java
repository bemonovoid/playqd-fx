package io.playqd.mini.controller.configurer;

import java.text.NumberFormat;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.playqd.data.Track;
import io.playqd.mini.controller.ItemsTableColumnIds;
import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.factories.ImageTableCellFactory;
import io.playqd.mini.controller.factories.NameTableCellFactory;
import io.playqd.mini.controller.factories.StatusTableCellFactory;
import io.playqd.mini.controller.factories.TrackImageTableCellFactory;
import io.playqd.mini.controller.factories.TrackStatusTableCellFactory;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.TrackItemRow;
import io.playqd.mini.controller.item.contextmenu.ContextMenuItemsBuilder;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import io.playqd.player.Player;
import io.playqd.player.TrackListRequest;
import io.playqd.utils.TimeUtils;

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
                    if (col.getId().equals(ItemsTableColumnIds.STATUS_COL)) {
                        @SuppressWarnings("unchecked")
                        var statusCol = (TableColumn<LibraryItemRow, String>) col;
                        statusCol.setCellValueFactory(c -> c.getValue().getStatus());
                    }
                    if (col.getId().equals(ItemsTableColumnIds.MISC_VALUE_COL)) {
                        @SuppressWarnings("unchecked")
                        var miscValueCol = (TableColumn<LibraryItemRow, String>) col;
                        miscValueCol.setCellValueFactory(c -> c.getValue().getMiscValue());
                    }
                });
    }

    @Override
    protected Set<String> getIncludedColumns() {
        return Set.of(ItemsTableColumnIds.STATUS_COL, ItemsTableColumnIds.MISC_VALUE_COL);
    }

    @Override
    protected Map<String, String> getColumnNameOverrides() {
        return Map.of(ItemsTableColumnIds.MISC_VALUE_COL, "Time");
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
    public void onOpen(TableView<LibraryItemRow> tableView) {
        var items = tableView.getSelectionModel().getSelectedItems();
        if (items.isEmpty()) {
            return;
        }
        if (items.getFirst() instanceof TrackItemRow trackItemRow) {
            Player.enqueue(new TrackListRequest(trackItemRow.getSource()));
        } else {
            LOG.error("Unexpected item type: {}. Expected type: {}", items.getFirst().getClass(),  TrackItemRow.class);
        }
    }

    @Override
    public List<MenuItem> configureContextMenuItems(List<LibraryItemRow> selectedItems) {
        var items = selectedItems.stream().map(i -> (Track) i.getSource()).toList();
        return ContextMenuItemsBuilder.newBuilder(controller)
                .playMenuItems(items)
                .addToQueueMenuItems(items)
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
    protected NameTableCellFactory getNameTableCellFactory() {
        return new NameTableCellFactory(this, libraryItemRow -> {
            if (libraryItemRow.getSource() instanceof Track track) {
                return track.artistName();
            }
            return null;
        });
    }

    @Override
    protected StatusTableCellFactory getStatusTableCellFactory() {
        return new TrackStatusTableCellFactory();
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

    protected final void enqueueAll(TableView<LibraryItemRow> tableView) {
        var selectedItems = tableView.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) {
            return;
        }
        if (selectedItems.getFirst() instanceof TrackItemRow) {
            if (selectedItems.size() == 1) {
                var selectedIdx = tableView.getSelectionModel().getSelectedIndex();
                var tracks = tableView.getItems().stream().map(item -> (Track) item.getSource()).toList();
                Player.enqueue(new TrackListRequest(selectedIdx, tracks));
            }
        }
    }

 }
