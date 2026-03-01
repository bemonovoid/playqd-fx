package io.playqd.controller.music;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.data.Track;
import io.playqd.dialog.tracks.TracksTableViewColumnsDialog;
import io.playqd.event.MouseEventHelper;
import io.playqd.player.PlayRequest;
import io.playqd.player.PlayerEngine;
import io.playqd.utils.FakeIds;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class MusicLibraryTracksController extends MusicLibraryAlbumsController {

    private static final Logger LOG = LoggerFactory.getLogger(MusicLibraryTracksController.class);

    private final Searchable searchable = new SearchTextController();
    private final ContextMenu columnContextMenu = createContextMenu();

    protected void initializeInternal() {
        super.initializeInternal();
        searchable.initialize(getTracksTableView(), onSearchTextInputChanged(), onSearchTextInputCleared());
        getTracksTableView().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        getTracksTableView().getColumns().forEach(col -> col.setContextMenu(columnContextMenu));
        getTracksTableView().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);
        getTracksTableView().setRowFactory(_ -> {
            var row = new TableRow<Track>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && MouseEventHelper.primaryButtonDoubleClicked(e)) {
                    PlayerEngine.enqueueAndPlay(new PlayRequest(getTracksTableView().getItems(), row.getIndex()));
                }
            });
            return row;
        });
    }

    private ContextMenu createContextMenu() {
        var contextMenu = new ContextMenu();
        var menuItem = new MenuItem("Configure columns ...", new FontAwesomeIconView(FontAwesomeIcon.COLUMNS));
        menuItem.setOnAction(_ -> showConfigColumnsDialog());
        contextMenu.getItems().add(menuItem);
        return contextMenu;
    }

    private void showConfigColumnsDialog() {
        var availableColumns = new ArrayList<String>();
        var visibleColumns = new ArrayList<String>();
        getTracksTableView().getColumns().forEach(col -> {
            var colName = col.getText();
            if (col.isVisible()) {
                visibleColumns.add(colName);
            } else {
                availableColumns.add(colName);
            }
        });
        new TracksTableViewColumnsDialog(availableColumns, visibleColumns).afterShowAndWait(selectedColumns -> {
            getTracksTableView().getColumns().forEach(col -> col.setVisible(selectedColumns.contains(col.getText())));
        });
    }

    private Consumer<String> onSearchTextInputChanged() {
        return newInput -> {
            getTracksTableView().getSelectionModel().clearSelection();
            if (newInput.isEmpty()) {
                tracksContainer.getTracksSearchLabel().setText("");
                tracksContainer.getTracksSearchLabel().setVisible(false);
                return;
            }
            @SuppressWarnings("unchecked")
            var itemsStream = ((List<Track>) getTracksTableView().getUserData()).stream()
                    .filter(track -> track.title().toLowerCase().contains(newInput));
            var selectedAlbum = getSelectedAlbum();
            if (selectedAlbum != null && !FakeIds.ALL_ARTIST_ALBUMS_NAME.equals(selectedAlbum.name())) {
                itemsStream = itemsStream
                        .filter(track ->
                                track.artistName().equals(selectedAlbum.artistName()) &&
                                        track.albumName().equals(selectedAlbum.name()));
            }
            var items = new ArrayList<>(itemsStream.toList());
            LOG.info("Search by: '{}'. Found: {}", newInput, items.size());
            items.sort(Comparator.comparing(t -> t.title().toLowerCase()));
            getTracksTableView().setItems(FXCollections.observableArrayList(items));
            getTracksTableView().getSelectionModel().selectFirst();
            if (!tracksContainer.getTracksSearchLabel().isVisible()) {
                tracksContainer.getTracksSearchLabel().setVisible(true);
            }
            tracksContainer.getTracksSearchLabel().setText(newInput);
        };
    }

    private Runnable onSearchTextInputCleared() {
        return () -> {
            @SuppressWarnings("unchecked")
            var sourceItems = (List<Track>) getTracksTableView().getUserData();
            var selectedAlbum = getSelectedAlbum();
            if (selectedAlbum != null && !FakeIds.ALL_ARTIST_ALBUMS_NAME.equals(selectedAlbum.name())) {
                sourceItems = sourceItems.stream()
                        .filter(track ->
                                track.artistName().equals(selectedAlbum.artistName()) &&
                                        track.albumName().equals(selectedAlbum.name()))
                        .toList();
            }
            getTracksTableView().setItems(FXCollections.observableArrayList(sourceItems));
            getTracksTableView().getSelectionModel().selectFirst();
        };
    }
}
