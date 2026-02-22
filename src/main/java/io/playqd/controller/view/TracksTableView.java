package io.playqd.controller.view;

import io.playqd.controller.music.TrackTimeTableCellFactory;
import io.playqd.data.Track;
import io.playqd.dialog.tracks.TracksTableViewColumnsDialog;
import io.playqd.event.MouseEventHelper;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import io.playqd.player.PlayRequest;
import io.playqd.player.PlayerEngine;
import io.playqd.utils.TimeUtils;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public class TracksTableView extends TableView<Track> {

    private final StringProperty tracksInfoProperty = new SimpleStringProperty("");
    private final ObjectProperty<TrackSelectedRow> rowDoubleClickedProperty = new SimpleObjectProperty<>();

    @FXML
    public TableColumn<Track, String> trackNumberCol, titleCol, artistCol, albumCol, filenameCol, sizeCol,
            genreCol, extensionCol, bitRateCol, sampleRateCol, bitsPerSampleCol, ratingCol, mimeTypeCol,
            playCountCol, lastPlayedDateCol, addedDateCol;
    @FXML
    private TableColumn<Track, Integer> timeCol;

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
        initItemsChangedListener();
        initKeyEventListeners();
        initRowFactories();
    }

    private void initTableProperties() {
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private void initColumnComparators() {
        timeCol.setComparator(Integer::compareTo);
    }

    private void intiColumnCellFactories() {
        timeCol.setCellFactory(new TrackTimeTableCellFactory());
    }

    private void initColumnCellValueFactories() {
        trackNumberCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().number()));
        titleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().title()));
        timeCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().length().seconds()));
        artistCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().artistName()));
        albumCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().albumName()));
        genreCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().genre()));
        filenameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fileAttributes().name()));
        sizeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fileAttributes().readableSize()));
        extensionCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fileAttributes().extension()));
        mimeTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().audioFormat().mimeType()));
        bitRateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().audioFormat().bitRate()));
        sampleRateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().audioFormat().sampleRate()));
        bitsPerSampleCol.setCellValueFactory(c -> new SimpleStringProperty("" + c.getValue().audioFormat().bitsPerSample()));
        ratingCol.setCellValueFactory(c -> new SimpleStringProperty("" + c.getValue().rating().value()));
        playCountCol.setCellValueFactory(c -> new SimpleStringProperty("" + c.getValue().playback().count()));
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

    private void initItemsChangedListener() {
        itemsProperty().addListener((_, _, newItems) -> {
            if (newItems == null || newItems.isEmpty()) {
                tracksInfoProperty.set("");
            } else {
                var totalSize = (long) 0;
                var totalLength = (long) 0;
                for (Track track : newItems) {
                    totalSize += track.fileAttributes().size();
                    totalLength += track.length().seconds();
                }
                var files = "" + newItems.size() + (newItems.size() > 1 ? " files" : " file");
                var sizeFormatted = org.apache.commons.io.FileUtils.byteCountToDisplaySize(totalSize);
                var lengthFormatted = TimeUtils.durationToTimeFormat(Duration.ofSeconds(totalLength));
                tracksInfoProperty.set(String.format("%s, %s, %s", files, sizeFormatted, lengthFormatted));
            }
        });
    }

    private void initKeyEventListeners() {
        setOnKeyPressed(keyEvent -> {
            var keyCode = keyEvent.getCode();
            if (keyEvent.isShortcutDown()) {
                if (keyEvent.isControlDown()) {
                    if (KeyCode.ENTER == keyCode) {
                        var items = getSelectionModel().getSelectedItems();
                        if (!items.isEmpty()) {
                            PlayerEngine.PLAYING_QUEUE.enqueue(Collections.unmodifiableList(items));
                        }
                    }
                }
            } else {
                if (KeyCode.ENTER == keyCode) {
                    var items = getSelectionModel().getSelectedItems();
                    if (!items.isEmpty()) {
                        PlayerEngine.enqueueAndPlay(new PlayRequest(Collections.unmodifiableList(items)));
                    }
                }
            }
        });
    }

    private void initRowFactories() {
        setRowFactory(_ -> {
            var row = new TableRow<Track>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && MouseEventHelper.primaryButtonDoubleClicked(e)) {
                    rowDoubleClickedProperty.set(new TrackSelectedRow(row.getIndex(), row.getItem()));
                }
            });
            return row;
        });
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

    public ReadOnlyStringProperty tracksInfoProperty() {
        return tracksInfoProperty;
    }

    public ReadOnlyObjectProperty<TrackSelectedRow> rowDoubleClickedProperty() {
        return rowDoubleClickedProperty;
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
}
