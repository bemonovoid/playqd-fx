package io.playqd.controller.view;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import io.playqd.controller.music.TrackTimeTableCellFactory;
import io.playqd.data.Track;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import io.playqd.utils.TimeUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public class TracksContainer extends VBox {

    @FXML
    private Label tracksSearchLabel;

    @FXML
    private MenuButton sortTracksMenuBtn;

    @FXML
    private TableView<Track> tracksTableView;

    @FXML
    public TableColumn<Track, String> trackNumberCol, titleCol, artistCol, albumCol, filenameCol, sizeCol,
            genreCol, extensionCol, bitRateCol, sampleRateCol, bitsPerSampleCol, ratingCol, mimeTypeCol,
            playCountCol, lastPlayedDateCol, addedDateCol;
    @FXML
    private TableColumn<Track, Integer> timeCol;

    @FXML
    private Label tracksInfoLabel;

    public TracksContainer() {
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.TRACKS_CONTAINER);
        resourceLoader.setRoot(this);
        resourceLoader.setController(this);
        FXMLLoaderUtils.loadObject(resourceLoader, TracksContainer.class);
    }

    @FXML
    public void initialize() {
        initTrackTableViewColumns();
        initSortTracksMenuButton();

        tracksTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tracksInfoLabel.setDisable(true);
        tracksInfoLabel.setStyle("-fx-font-size: 11px;");
        tracksInfoLabel.setOpacity(0.6);

        tracksTableView.itemsProperty().addListener((_, _, newItems) -> {
            if (newItems == null || newItems.isEmpty()) {
                tracksInfoLabel.setText("");
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
                tracksInfoLabel.setText(String.format("%s, %s, %s", files, sizeFormatted, lengthFormatted));
            }
        });
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
            tracksTableView.setUserData(Collections.unmodifiableList(allTracks));
            tracksTableView.setItems(FXCollections.observableList(allTracks));
        });
    }

    private void initTrackTableViewColumns() {
        trackNumberCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().number()));
        titleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().title()));

        timeCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().length().seconds()));
        timeCol.setCellFactory(new TrackTimeTableCellFactory());
        timeCol.setComparator(Integer::compareTo);

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

    private void initSortTracksMenuButton() {
        sortTracksMenuBtn.getStyleClass().setAll("button");
        var byNameAscMenuItem = new CustomMenuItem(FontAwesomeIconFactory.get().createIconLabel(
                FontAwesomeIcon.SORT_ALPHA_ASC, "Name ascending", "10px", "12px", ContentDisplay.LEFT));
        var byNameDescMenuItem = new CustomMenuItem(FontAwesomeIconFactory.get().createIconLabel(
                FontAwesomeIcon.SORT_ALPHA_DESC, "Name descending", "10px", "12px", ContentDisplay.LEFT));
        var byAlbumCountAscMenuItem = new CustomMenuItem(FontAwesomeIconFactory.get().createIconLabel(
                FontAwesomeIcon.SORT_AMOUNT_ASC, "Albums count ascending", "10px", "12px", ContentDisplay.LEFT));
        var byAlbumCountDescMenuItem = new CustomMenuItem(FontAwesomeIconFactory.get().createIconLabel(
                FontAwesomeIcon.SORT_AMOUNT_DESC, "Albums count descending", "10px", "12px", ContentDisplay.LEFT));
        var byTracksCountAscMenuItem = new CustomMenuItem(FontAwesomeIconFactory.get().createIconLabel(
                FontAwesomeIcon.SORT_AMOUNT_ASC, "Tracks count ascending", "10px", "12px", ContentDisplay.LEFT));
        var byTracksCountDescMenuItem = new CustomMenuItem(FontAwesomeIconFactory.get().createIconLabel(
                FontAwesomeIcon.SORT_AMOUNT_DESC, "Tracks count descending", "10px", "12px", ContentDisplay.LEFT));

        byNameAscMenuItem.setOnAction(_ -> sort(Comparator.comparing(Track::title), false));
        byNameDescMenuItem.setOnAction(_ -> sort(Comparator.comparing(Track::title), true));

//        byAlbumCountAscMenuItem.setOnAction(_ -> sort(Comparator.comparing(Artist::albumsCount), false));
//        byAlbumCountDescMenuItem.setOnAction(_ -> sort(Comparator.comparing(Artist::albumsCount), true));
//
//        byTracksCountAscMenuItem.setOnAction(_ -> sort(Comparator.comparing(Artist::tracksCount), false));
//        byTracksCountDescMenuItem.setOnAction(_ -> sort(Comparator.comparing(Artist::tracksCount), true));

        sortTracksMenuBtn.getItems().addAll(byNameAscMenuItem, byNameDescMenuItem, new SeparatorMenuItem(),
                byAlbumCountAscMenuItem, byAlbumCountDescMenuItem, new SeparatorMenuItem(),
                byTracksCountAscMenuItem, byTracksCountDescMenuItem);
    }

    public void clearTracksTable() {
        tracksTableView.getItems().clear();
    }

    public TableView<Track> getTracksTableView() {
        return tracksTableView;
    }

    public Label getTracksSearchLabel() {
        return tracksSearchLabel;
    }

    private void sort(Comparator<Track> comparator, boolean reversed) {
        var allArtistsItem = tracksTableView.getItems().getFirst();
        var artistItems = tracksTableView.getItems().subList(1, tracksTableView.getItems().size());
        if (reversed) {
            artistItems.sort(comparator.reversed());
        } else {
            artistItems.sort(comparator);
        }
        var sortedItems = new ArrayList<Track>(tracksTableView.getItems().size());
        sortedItems.add(allArtistsItem);
        sortedItems.addAll(artistItems);
        tracksTableView.setItems(FXCollections.observableArrayList(sortedItems));
    }

    private static String adjustTrackDisplayLength(String trackLengthReadable) {
        if (trackLengthReadable.indexOf(':') > 1 && trackLengthReadable.startsWith("0")) {
            return trackLengthReadable.substring(1);
        }
        return trackLengthReadable;
    }
}
