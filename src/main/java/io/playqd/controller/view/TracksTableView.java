package io.playqd.controller.view;

import io.playqd.controller.view.menuitem.TrackRowContextMenuItemsFactory;
import io.playqd.data.PlaylistWithTrackIds;
import io.playqd.data.Track;
import io.playqd.dialog.tracks.TracksTableViewColumnsDialog;
import io.playqd.event.MouseEventHelper;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import io.playqd.player.PlayerTrackListManager;
import io.playqd.player.TrackListRequest;
import io.playqd.service.MusicLibrary;
import io.playqd.service.TrackComparators;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TracksTableView extends TableView<TrackTableRow> {

    private static final Logger LOG = LoggerFactory.getLogger(TracksTableView.class);

    private final StringProperty selectedTracksInfoProperty = new SimpleStringProperty("");
    private final StringProperty tracksInfoProperty = new SimpleStringProperty("");
    private final ObjectProperty<TrackSelectedRow> rowDoubleClickedProperty = new SimpleObjectProperty<>();

    private TracksDisplayOptions displayOptions;
    private Supplier<TrackRowContextMenuItemsFactory> trackContextMenuItemsFactory;

    @FXML
    public TableColumn<TrackTableRow, Long> artworkCol, sizeCol;

    @FXML
    public TableColumn<TrackTableRow, String> titleCol, trackNumberCol;

    @FXML
    public TableColumn<TrackTableRow, Integer> timeCol, ratingCol, playCountCol, sampleRateCol, bitRateCol, bitsPerSampleCol;

    @FXML
    private TableColumn<TrackTableRow, LocalDateTime> ratedDateCol, lastPlayedDateCol, addedDateCol;

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
        initItemsListChangedListener();
        initTracksUpdatedListener();
        initKeyEventListeners();
        initRowFactories();
    }

    private void initTableProperties() {
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private void initColumnComparators() {
        trackNumberCol.setComparator(TrackComparators.trackNumberComparator());
        timeCol.setComparator(Integer::compareTo);
        sizeCol.setComparator(Long::compareTo);
        sampleRateCol.setComparator(Integer::compareTo);
        ratedDateCol.setComparator(LocalDateTime::compareTo);
        lastPlayedDateCol.setComparator(LocalDateTime::compareTo);
        addedDateCol.setComparator(Comparator.reverseOrder());
    }

    private void intiColumnCellFactories() {
        artworkCol.setCellFactory(new TrackArtworkTableCellFactory());
        bitRateCol.setCellFactory(new NumberFormatTableCellFactory(m -> m.track().audioFormat().bitRate()));
        sampleRateCol.setCellFactory(new NumberFormatTableCellFactory(m -> m.track().audioFormat().sampleRate()));
        bitsPerSampleCol.setCellFactory(new NumberFormatTableCellFactory(m -> m.track().audioFormat().bitsPerSample()));
        sizeCol.setCellFactory(new TrackSizeFormatTableCellFactory());
        timeCol.setCellFactory(new TrackTimeTableCellFactory());
        ratedDateCol.setCellFactory(new TrackDateTypeTableCellFactory());
        lastPlayedDateCol.setCellFactory(new TrackDateTypeTableCellFactory());
        addedDateCol.setCellFactory(new TrackDateTypeTableCellFactory());
    }

    private void initColumnCellValueFactories() {
        ratingCol.setCellValueFactory(c -> c.getValue().getRating().asObject());
        ratedDateCol.setCellValueFactory(c -> c.getValue().getRatedDate());
        playCountCol.setCellValueFactory(c -> c.getValue().getPlayCount().asObject());
        lastPlayedDateCol.setCellValueFactory(c -> c.getValue().getLastPlayedDate());

    }

    private void initSelectedItemsChangedListener() {
        getSelectionModel().getSelectedItems().addListener((ListChangeListener<TrackTableRow>) changed ->
                updateSelectedTracksInfoProperty(changed == null ? Collections.emptyList() : changed.getList()));
    }

    private void initItemsListChangedListener() {
        itemsProperty().addListener((_, _, newItems) -> updateTracksInfoProperty(newItems));
    }

    private void initTracksUpdatedListener() {
        // This will only update currently displayed table items. If the items must be removed or added this is to be
        // done in the class that holds the context about an action, e.g: Unlike a track in favorites view will remove
        // an item from table view. This handler is only responsible to update observable properties.
        MusicLibrary.tracksUpdatedEventProperty().addListener((_, _, tracksUpdate) -> {
            var updatedTracks = tracksUpdate.tracks().stream()
                    .collect(Collectors.toMap(Track::id, Function.identity()));
            for (var m : getItems()) {
                if (updatedTracks.isEmpty()) {
                    break;
                }
                var updatedTrack = updatedTracks.get(m.track().id());
                if (updatedTrack != null) {
                    m.setObservableProperties(updatedTrack);
                    updatedTracks.remove(m.track().id());
                }
            }
        });
    }

    private void updateSelectedTracksInfoProperty(List<? extends TrackTableRow> changed) {
        var selected = changed == null ? 0 : changed.size();
        var time = "";
        if (selected > 0) {
            var totalSeconds = changed.stream().mapToInt(m -> m.track().length().seconds()).sum();
            time = TimeUtils.durationToTimeFormat(Duration.ofSeconds(totalSeconds));
        }
        var text = String.format("Selected: %s, time: %s", selected, time);
        selectedTracksInfoProperty.set(text);
    }

    private void updateTracksInfoProperty(List<TrackTableRow> trackRows) {
        if (trackRows == null || trackRows.isEmpty()) {
            tracksInfoProperty.set("");
        } else {
            var totalSize = (long) 0;
            var totalLength = (long) 0;
            for (var m : trackRows) {
                totalSize += m.track().fileAttributes().size();
                totalLength += m.track().length().seconds();
            }
            var files = Numbers.format(trackRows.size()) + (trackRows.size() > 1 ? " files" : " file");
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
                            var trackListReq = new TrackListRequest(items.stream().map(TrackTableRow::track).toList());
                            PlayerTrackListManager.enqueue(trackListReq);
                        }
                    }
                }
            } else {
                if (KeyCode.ENTER == keyCode) {
                    var items = getSelectionModel().getSelectedItems();
                    if (!items.isEmpty()) {
                        var trackListReq = new TrackListRequest(items.stream().map(TrackTableRow::track).toList());
                        PlayerTrackListManager.enqueue(trackListReq);
                    }
                }
            }
        });
    }

    private void initRowFactories() {
        setRowFactory(_ -> {
            var row = new TableRow<TrackTableRow>();
            setOnRowMouseClicked(row);
            setOnRowItemChanged(row);
            return row;
        });
    }

    private void setOnRowMouseClicked(TableRow<TrackTableRow> row) {
        row.setOnMouseClicked(e -> {
            if (!row.isEmpty()) {
                if (MouseEventHelper.primaryButtonDoubleClicked(e)) {
                    rowDoubleClickedProperty.set(new TrackSelectedRow(row.getIndex(), row.getItem().track()));
                } else if (MouseEventHelper.secondaryButtonSingleClicked(e)) {
                    if (row.getContextMenu() == null) {
                        var contextMenu = new TrackRowContextMenu(getTrackContextMenuItemsFactory());
                        contextMenu.setOnHidden(_ -> row.setContextMenu(null)); // to reset a state
                        row.setContextMenu(contextMenu);
                        contextMenu.show(row, e.getScreenX(), e.getScreenY());
                    }
                }
            }
        });
    }

    private void setOnRowItemChanged(TableRow<TrackTableRow> row) {
//        row.itemProperty().addListener((_, _, newItem) -> {
//            row.setDisable(true);
//        });
    }

    public void setTrackContextMenuItemsFactory(Supplier<TrackRowContextMenuItemsFactory> factory) {
        if (this.trackContextMenuItemsFactory != null) {
            LOG.warn("'trackContextMenuItemsFactory' was already set.");
        } else {
            this.trackContextMenuItemsFactory = factory;
            LOG.info("'trackContextMenuItemsFactory' was set.");
        }
    }

    public void clearTracksTable() {
        getItems().clear();
    }

    public void showTracks(Supplier<List<Track>> tracks) {
        showTracks(tracks, new TracksDisplayOptions());
    }

    public void showTracks(Supplier<List<Track>> tracks, TracksDisplayOptions displayOptions) {
        showTracks(tracks, displayOptions, null);
    }

    public void showTracks(Supplier<List<Track>> tracks, Comparator<Track> comparator) {
        showTracks(tracks, new TracksDisplayOptions(), comparator);
    }

    private void showTracks(Supplier<List<Track>> tracksProvider,
                            TracksDisplayOptions displayOptions,
                            Comparator<Track> comparator) {
        this.displayOptions = displayOptions;
        Platform.runLater(() -> {
            var allTracks = tracksProvider.get();
            if (comparator != null) {
                allTracks.sort(comparator);
            }
            var trackRows = allTracks.stream()
                    .filter(track -> !track.isCueParentTrack())
                    .map(TrackTableRow::new)
                    .toList();
            setUserData(trackRows);
            setItems(FXCollections.observableArrayList(trackRows));
            if (!trackRows.isEmpty()) {
                scrollTo(0);
            }
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

    public Track getSelectedTrack() {
        return getSelectionModel().getSelectedItem().track();
    }

    public List<Track> getSelectedTracks() {
        return getSelectionModel().getSelectedItems().stream().map(TrackTableRow::track).toList();
    }

    public List<Track> getItemsAsTracks() {
        return getItems().stream().map(TrackTableRow::track).toList();
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
        var trackIds = getSelectionModel().getSelectedItems().stream().map(m -> m.track().id()).toList();
        MusicLibrary.addTracksToPlaylist(playlistId, trackIds);
    }

    private TrackRowContextMenuItemsFactory getTrackContextMenuItemsFactory() {
        var factory = (TrackRowContextMenuItemsFactory) null;
        if (trackContextMenuItemsFactory != null) {
            factory = trackContextMenuItemsFactory.get();
        }
        if (factory == null) {
            factory = new TrackRowContextMenuItemsFactory();
        }
        return factory;
    }
}
