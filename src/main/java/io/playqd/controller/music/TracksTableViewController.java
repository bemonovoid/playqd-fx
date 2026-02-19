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

class TracksTableViewController extends SearchableView {

    private static final Logger LOG = LoggerFactory.getLogger(TracksTableViewController.class);

    private final MusicSplitPaneController musicController;
    private final TableView<Track> tracksTableView;
    private final ContextMenu columnContextMenu;

    TracksTableViewController(MusicSplitPaneController musicController) {
        this.musicController = musicController;
        this.tracksTableView = musicController.getTracksContainer().getTracksTableView();
        this.columnContextMenu = createContextMenu();
    }

    void initialize() {
        super.initialize(tracksTableView, onSearchTextInputChanged(), onSearchTextInputCleared());
        tracksTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tracksTableView.getColumns().forEach(col -> col.setContextMenu(columnContextMenu));
        tracksTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);
        tracksTableView.setRowFactory(_ -> {
            var row = new TableRow<Track>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && MouseEventHelper.primaryButtonDoubleClicked(e)) {
                    PlayerEngine.enqueueAndPlay(new PlayRequest(tracksTableView.getItems(), row.getIndex()));
                }
            });
            return row;
        });
    }

    @Override
    public Consumer<String> onSearchTextInputChanged() {
        return newInput -> {
            tracksTableView.getSelectionModel().clearSelection();
            if (newInput.isEmpty()) {
                return;
            }
            @SuppressWarnings("unchecked")
            var itemsStream = ((List<Track>) tracksTableView.getUserData()).stream()
                    .filter(track -> track.title().toLowerCase().contains(newInput));
            var selectedAlbum = musicController.getSelectedAlbum();
            if (selectedAlbum != null && !FakeIds.ALL_ARTIST_ALBUMS.equals(selectedAlbum.id())) {
                itemsStream = itemsStream
                        .filter(track ->
                                track.artistName().equals(selectedAlbum.artistName()) &&
                                track.albumName().equals(selectedAlbum.name()));
            }
            var items = new ArrayList<>(itemsStream.toList());
            LOG.info("Search by: '{}'. Found: {}", newInput, items.size());
            items.sort(Comparator.comparing(t -> t.title().toLowerCase()));
            tracksTableView.setItems(FXCollections.observableArrayList(items));
            tracksTableView.getSelectionModel().selectFirst();
        };
    }

    @Override
    public Runnable onSearchTextInputCleared() {
        return () -> {
            @SuppressWarnings("unchecked")
            var sourceItems = (List<Track>) tracksTableView.getUserData();
            var selectedAlbum = musicController.getSelectedAlbum();
            if (selectedAlbum != null && !FakeIds.ALL_ARTIST_ALBUMS.equals(selectedAlbum.id())) {
                sourceItems = sourceItems.stream()
                        .filter(track ->
                                track.artistName().equals(selectedAlbum.artistName()) &&
                                track.albumName().equals(selectedAlbum.name()))
                        .toList();
            }
            tracksTableView.setItems(FXCollections.observableArrayList(sourceItems));
            tracksTableView.getSelectionModel().selectFirst();
        };
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
        tracksTableView.getColumns().forEach(col -> {
            var colName = col.getText();
            if (col.isVisible()) {
                visibleColumns.add(colName);
            } else {
                availableColumns.add(colName);
            }
        });
        new TracksTableViewColumnsDialog(availableColumns, visibleColumns).afterShowAndWait(selectedColumns -> {
            tracksTableView.getColumns().forEach(col -> col.setVisible(selectedColumns.contains(col.getText())));
        });
    }
}
