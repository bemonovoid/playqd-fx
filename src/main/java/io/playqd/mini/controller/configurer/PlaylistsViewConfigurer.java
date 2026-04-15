package io.playqd.mini.controller.configurer;

import io.playqd.controller.playlists.PlaylistDialog;
import io.playqd.mini.controller.ItemsTableColumnIds;
import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.NavigableItemsResolver;
import io.playqd.mini.controller.factories.*;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.PlaylistItemRow;
import io.playqd.mini.controller.item.PlaylistTrackItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

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
    protected DescriptionTableCellFactory getDescriptionTableCellFactory() {
        return null;
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
    protected void configureHeaderRight(TableView<LibraryItemRow> tableView, HBox headerRight) {
        var createNew = new Hyperlink("Create new");
        createNew.setFocusTraversable(false);
        createNew.setOnAction(_ -> {
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
        headerRight.getChildren().add(createNew);
    }

    @Override
    public void onRowOpened(LibraryItemRow item) {
        if (item instanceof PlaylistItemRow playlistItemRow) {
            controller.showItems(NavigableItemsResolver.resolvePlaylistTracks(playlistItemRow));
        } else {
            LOG.error("Unexpected item type: {}. Expected type: {}", item.getClass(), PlaylistTrackItemRow.class);
        }
    }

    @Override
    protected Set<String> getExcludedColumns() {
        return Set.of(ItemsTableColumnIds.DESCRIPTION_COL);
    }
}
