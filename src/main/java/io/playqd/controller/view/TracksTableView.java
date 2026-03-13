package io.playqd.controller.view;

import io.playqd.controller.playlists.PlaylistDialog;
import io.playqd.controller.view.menuitem.TrackContextMenuConfigurer;
import io.playqd.data.PlaylistWithTrackIds;
import io.playqd.data.Track;
import io.playqd.dialog.tracks.TracksTableViewColumnsDialog;
import io.playqd.event.MouseEventHelper;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import io.playqd.player.PlayRequest;
import io.playqd.player.Player;
import io.playqd.service.MusicLibrary;
import io.playqd.utils.Numbers;
import io.playqd.utils.TimeUtils;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public class TracksTableView extends TableView<Track> {

    private static final Logger LOG = LoggerFactory.getLogger(TracksTableView.class);

    private final StringProperty selectedTracksInfoProperty = new SimpleStringProperty("");
    private final StringProperty tracksInfoProperty = new SimpleStringProperty("");
    private final ObjectProperty<TrackSelectedRow> rowDoubleClickedProperty = new SimpleObjectProperty<>();
    private Supplier<TrackContextMenuConfigurer> trackContextMenuConfigurerFactory;

    @FXML
    public TableColumn<Track, String> trackNumberCol, titleCol, artistCol, albumCol, filenameCol, sizeCol,
            genreCol, extensionCol, ratingCol, mimeTypeCol,
            playCountCol, lastPlayedDateCol, addedDateCol;
    @FXML
    public TableColumn<Track, Integer> timeCol, sampleRateCol, bitRateCol, bitsPerSampleCol;

    @FXML
    public Menu addToPlaylistMenu;

    public TracksTableView() {
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.TRACKS_TABLE_VIEW);
        resourceLoader.setRoot(this);
        resourceLoader.setController(this);
        FXMLLoaderUtils.loadObject(resourceLoader, TracksTableView.class);
    }

    @FXML
    private void initialize() {
        initTableProperties();
        initColumnComparators();
        intiColumnCellFactories();
        initColumnCellValueFactories();
        initSelectedItemsChangedListener();
        initItemsChangedListener();
        initKeyEventListeners();
        initRowFactories();
    }

    private void initTableProperties() {
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private void initColumnComparators() {
        timeCol.setComparator(Integer::compareTo);
        sampleRateCol.setComparator(Integer::compareTo);
    }

    private void intiColumnCellFactories() {
        bitRateCol.setCellFactory(new NumberFormatTableCellFactory(t -> t.audioFormat().bitRate()));
        sampleRateCol.setCellFactory(new NumberFormatTableCellFactory(t -> t.audioFormat().sampleRate()));
        timeCol.setCellFactory(new TrackTimeTableCellFactory());
    }

    private void initColumnCellValueFactories() {
        // SimpleObjectProperty
        timeCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().length().seconds()));
        bitRateCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().audioFormat().bitRate()));
        sampleRateCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().audioFormat().sampleRate()));
        bitsPerSampleCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().audioFormat().bitsPerSample()));
        // SimpleStringProperty
        trackNumberCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().number()));
        titleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().title()));
        artistCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().artistName()));
        albumCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().albumName()));
        genreCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().genre()));
        filenameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fileAttributes().name()));
        extensionCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fileAttributes().extension()));
        mimeTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().audioFormat().mimeType()));
        ratingCol.setCellValueFactory(c -> new SimpleStringProperty("" + c.getValue().rating().value()));
        playCountCol.setCellValueFactory(c -> new SimpleStringProperty("" + c.getValue().playback().count()));

        sizeCol.setCellValueFactory(c -> {
            var displaySize = c.getValue().fileAttributes().readableSize();
            if (c.getValue().fileAttributes().size() == 0) {
                displaySize = "";
            }
            return new SimpleStringProperty(displaySize);
        });
        lastPlayedDateCol.setCellValueFactory(c -> {
            if (c.getValue().playback().lastPlayedDate() != null) {
                var date = c.getValue().playback().lastPlayedDate();
                return new SimpleStringProperty(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
            return new SimpleStringProperty("");
        });
        addedDateCol.setCellValueFactory(c -> {
            if (c.getValue().additionalInfo().addedToWatchFolderDate() != null) {
                var date = c.getValue().additionalInfo().addedToWatchFolderDate();
                return new SimpleStringProperty(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
            return new SimpleStringProperty("");
        });
    }

    private void initSelectedItemsChangedListener() {
        getSelectionModel().getSelectedItems().addListener((ListChangeListener<Track>) changed ->
                updateSelectedTracksInfoProperty(changed == null ? Collections.emptyList() : changed.getList()));
    }

    private void initItemsChangedListener() {
        itemsProperty().addListener((_, _, newItems) -> updateTracksInfoProperty(newItems));
    }

    private void updateSelectedTracksInfoProperty(List<? extends Track> changed) {
        var selected = changed == null ? 0 : changed.size();
        var time = "";
        if (selected > 0) {
            var totalSeconds = changed.stream().mapToInt(t -> t.length().seconds()).sum();
            time = TimeUtils.durationToTimeFormat(Duration.ofSeconds(totalSeconds));
        }
        var text = String.format("Selected: %s, time: %s", selected, time);
        selectedTracksInfoProperty.set(text);
    }

    private void updateTracksInfoProperty(List<Track> newTracks) {
        if (newTracks == null || newTracks.isEmpty()) {
            tracksInfoProperty.set("");
        } else {
            var totalSize = (long) 0;
            var totalLength = (long) 0;
            for (var track : newTracks) {
                totalSize += track.fileAttributes().size();
                totalLength += track.length().seconds();
            }
            var files = Numbers.format(newTracks.size()) + (newTracks.size() > 1 ? " files" : " file");
            var sizeFormatted = org.apache.commons.io.FileUtils.byteCountToDisplaySize(totalSize);
            var lengthFormatted = TimeUtils.durationToTimeFormat(Duration.ofSeconds(totalLength));
            tracksInfoProperty.set(String.format("%s, %s, %s", files, sizeFormatted, lengthFormatted));
        }
    }

    private void initKeyEventListeners() {
        setOnKeyPressed(keyEvent -> {
            var keyCode = keyEvent.getCode();
            if (keyEvent.isShortcutDown()) {
                if (keyEvent.isControlDown()) {
                    if (KeyCode.ENTER == keyCode) {
                        var items = getSelectionModel().getSelectedItems();
                        if (!items.isEmpty()) {
                            Player.PLAYING_QUEUE.enqueue(Collections.unmodifiableList(items));
                        }
                    }
                }
            } else {
                if (KeyCode.ENTER == keyCode) {
                    var items = getSelectionModel().getSelectedItems();
                    if (!items.isEmpty()) {
                        Player.enqueueAndPlay(new PlayRequest(Collections.unmodifiableList(items)));
                    }
                }
            }
        });
    }

    private void initRowFactories() {
        setRowFactory(_ -> {
            var row = new TableRow<Track>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty()) {
                    if (MouseEventHelper.primaryButtonDoubleClicked(e)) {
                        rowDoubleClickedProperty.set(new TrackSelectedRow(row.getIndex(), row.getItem()));
                    } else if (MouseEventHelper.secondaryButtonSingleClicked(e)) {
                        if (row.getContextMenu() == null) {
                            var contextMenu = new TrackRowContextMenu(getTrackContextMenuConfigurer());
                            contextMenu.setOnHidden(_ -> row.setContextMenu(null)); // to reset a state
                            row.setContextMenu(contextMenu);
                            contextMenu.show(row, e.getScreenX(), e.getScreenY());
                        }
                    }
                }
            });
            return row;
        });
    }

    public void setTrackContextMenuConfigurerFactory(Supplier<TrackContextMenuConfigurer> factory) {
        if (this.trackContextMenuConfigurerFactory != null) {
            LOG.warn("'trackContextMenuConfigurerFactory' was already set.");
        } else {
            this.trackContextMenuConfigurerFactory = factory;
            LOG.info("'trackContextMenuConfigurerFactory' was set.");
        }
    }

    public void clearTracksTable() {
        getItems().clear();
    }

    public void showTracks(Supplier<List<Track>> tracksProvider) {
        showTracks(tracksProvider, null);
    }

    public void showTracks(Supplier<List<Track>> tracksProvider, Comparator<Track> comparator) {
        Platform.runLater(() -> {
            var allTracks = tracksProvider.get();
            if (comparator != null) {
                allTracks.sort(comparator);
            }
            setUserData(Collections.unmodifiableList(allTracks));
            setItems(FXCollections.observableList(allTracks));
        });
    }

    public ReadOnlyStringProperty selectedTracksInfoProperty() {
        return selectedTracksInfoProperty;
    }

    public ReadOnlyStringProperty tracksInfoProperty() {
        return tracksInfoProperty;
    }

    public ReadOnlyObjectProperty<TrackSelectedRow> rowDoubleClickedProperty() {
        return rowDoubleClickedProperty;
    }

    public List<Track> getSelectedTracks() {
        return getSelectionModel().getSelectedItems();
    }

    @FXML
    private void configureColumns() {
        var availableColumns = new ArrayList<String>();
        var visibleColumns = new ArrayList<String>();
        getColumns().forEach(col -> {
            var colName = col.getText();
            if (col.isVisible()) {
                visibleColumns.add(colName);
            } else {
                availableColumns.add(colName);
            }
        });
        new TracksTableViewColumnsDialog(availableColumns, visibleColumns).afterShowAndWait(selectedColumns ->
                getColumns().forEach(col -> col.setVisible(selectedColumns.contains(col.getText()))));
    }

    private List<MenuItem> createAddToPlaylistMenuItems() {
        return MusicLibrary.getPlaylists().stream()
                .sorted(Comparator.comparing(PlaylistWithTrackIds::name))
                .map(p -> {
                    var menuItem = new MenuItem(p.name());
                    menuItem.setOnAction(_ -> addSelectedTracksToPlaylist(p.id()));
                    return menuItem;
                })
                .toList();
    }

    private void addSelectedTracksToPlaylist(long playlistId) {
        var trackIds = getSelectionModel().getSelectedItems().stream().map(Track::id).toList();
        MusicLibrary.addTracksToPlaylist(playlistId, trackIds);
    }

    private TrackContextMenuConfigurer getTrackContextMenuConfigurer() {
        var configurer = (TrackContextMenuConfigurer) null;
        if (trackContextMenuConfigurerFactory != null) {
            configurer = trackContextMenuConfigurerFactory.get();
        }
        if (configurer == null) {
            configurer = new TrackContextMenuConfigurer();
        }
        return configurer;
    }
}
