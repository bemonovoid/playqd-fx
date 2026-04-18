package io.playqd.mini.controller.configurer;

import io.playqd.controller.playlists.PlaylistDialog;
import io.playqd.mini.controller.ItemsTableColumnIds;
import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.NavigableItemsResolver;
import io.playqd.mini.controller.factories.ImageTableCellFactory;
import io.playqd.mini.controller.factories.MiscValueTableCellFactory;
import io.playqd.mini.controller.factories.PlaylistImageTableCellFactory;
import io.playqd.mini.controller.factories.TracksCountTableCellFactory;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.PlaylistItemRow;
import io.playqd.mini.controller.item.PlaylistTrackItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import io.playqd.mini.custom.ConfirmDeleteRowItemsDialog;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class PlaylistsViewConfigurer extends DefaultItemsViewConfigurer {

    private final static Logger LOG = LoggerFactory.getLogger(PlaylistsViewConfigurer.class);

    public PlaylistsViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    protected ImageTableCellFactory geImageTableCellFactory() {
        return new PlaylistImageTableCellFactory();
    }

    @Override
    protected MiscValueTableCellFactory getMiscValueTableCellFactory() {
        return new TracksCountTableCellFactory(libraryItemRow -> {
            if (libraryItemRow instanceof PlaylistItemRow playlistItemRow) {
                return playlistItemRow.getSource().tracks().size();
            }
            LOG.trace("Unexpected row type: {}", libraryItemRow.getClass());
            return -1;
        });
    }

    @Override
    protected void configureHeaderLeft(ItemsDescriptor itemsDescriptor, HBox headerLeft) {
        headerLeft.getChildren().add(new Label("Playlists:"));
    }

    @Override
    public Supplier<List<MenuItem>> configureViewOptionsMenuItems(TableView<LibraryItemRow> tableView) {
        return () -> {
            var newPlaylistMenuItem = new MenuItem("New Playlist", new FontIcon("fas-plus"));
            newPlaylistMenuItem.setOnAction(_ -> {
                var dialog = new PlaylistDialog();
                dialog.showAndWait().ifPresent(name -> {
                    if (!name.trim().isEmpty()) {
                        var playlistItemRow = new PlaylistItemRow(MusicLibrary.createPlaylist(name));
                        tableView.getItems().add(playlistItemRow);
                        tableView.refresh();
                        tableView.getSelectionModel().select(playlistItemRow);
                    }
                });
            });
            var deleteEmptyMenuItems = new MenuItem("Delete empty playlists", new FontIcon("fas-times"));
            deleteEmptyMenuItems.setOnAction(_ -> {
                var emptyPlaylists = tableView.getItems().stream()
                        .map(p -> (PlaylistItemRow) p)
                        .filter(p -> p.getSource().tracks().isEmpty())
                        .collect(Collectors.toMap(PlaylistItemRow::getId, p -> p));
                var confirmed = ConfirmDeleteRowItemsDialog.confirmDelete(new ArrayList<>(emptyPlaylists.values()));
                if (confirmed) {
                    MusicLibrary.deletePlaylists(new ArrayList<>(emptyPlaylists.keySet()));
                    controller.refreshLastState();
                }
            });
            return List.of(newPlaylistMenuItem, deleteEmptyMenuItems);
        };
    }

    @Override
    public void onItemsOpen(List<LibraryItemRow> items) {
        if (items.getFirst() instanceof PlaylistItemRow playlistItemRow) {
            controller.showItems(NavigableItemsResolver.resolvePlaylistTracks(playlistItemRow));
        } else {
            LOG.error("Unexpected item type: {}. Expected type: {}", items.getFirst().getClass(), PlaylistTrackItemRow.class);
        }
    }

    @Override
    public Optional<Consumer<List<LibraryItemRow>>> onItemsDelete() {
        return Optional.of(libraryItemRows -> libraryItemRows.stream()
                .filter(item -> item instanceof PlaylistItemRow)
                .forEach(item -> MusicLibrary.deletePlaylist(item.getId())));
    }

    @Override
    protected Set<String> getExcludedColumns() {
        return Set.of(ItemsTableColumnIds.DESCRIPTION_COL);
    }
}
