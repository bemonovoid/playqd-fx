package io.playqd.controller.view;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.controller.library.SearchTextController;
import io.playqd.controller.library.Searchable;
import io.playqd.data.Track;
import io.playqd.dialog.tracks.TracksTableViewColumnsDialog;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TracksView extends VBox {

    private static final Logger LOG = LoggerFactory.getLogger(TracksView.class);

    private final Searchable searchable = new SearchTextController();
    private final ContextMenu columnContextMenu = createContextMenu();

    @FXML
    private TracksTableHeader tracksTableHeader;

    @FXML
    private TracksTableView tracksTableView;

    @FXML
    private TracksTableFooter tracksTableFooter;

    public TracksView() {
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.TRACKS_VIEW);
        resourceLoader.setRoot(this);
        resourceLoader.setController(this);
        FXMLLoaderUtils.loadObject(resourceLoader, TracksView.class);
    }

    public void showTracks(Supplier<List<Track>> tracks) {
        showTracks(tracks, new TracksDisplayOptions());
    }

    public void showTracks(Supplier<List<Track>> tracks, TracksDisplayOptions displayOptions) {
        tracksTableView.showTracks(tracks, displayOptions);
    }

    @FXML
    private void initialize() {
        searchable.initialize(tracksTableView, onSearchTextInputChanged(), onSearchTextInputCleared());
        tracksTableView.getColumns().forEach(col -> col.setContextMenu(columnContextMenu));
        setTracksTableHeaderEventHandlers();
        setTracksTableFooterEventHandlers();
    }

    public void clear() {
        tracksTableView.showTracks(Collections::emptyList);
    }

    public TracksTableHeader tracksTableHeader() {
        return tracksTableHeader;
    }

    public TracksTableView tracksTableView() {
        return tracksTableView;
    }

    public TracksTableFooter tracksTableFooter() {
        return tracksTableFooter;
    }

    private void setTracksTableHeaderEventHandlers() {
        tracksTableHeader.setDisplayedColumnsMenuItem.setOnAction(_ -> showConfigColumnsDialog());
    }

    private void setTracksTableFooterEventHandlers() {
        tracksTableFooter.selectedLabel().textProperty().bind(tracksTableView.selectedTracksInfoProperty());
        tracksTableFooter.infoLabel().textProperty().bind(tracksTableView.tracksInfoProperty());
    }

    private Consumer<String> onSearchTextInputChanged() {
        return newInput -> {
            tracksTableView.getSelectionModel().clearSelection();
            if (newInput.isEmpty()) {
                tracksTableHeader.tracksSearchLabel().setText("");
                tracksTableHeader.tracksSearchLabel().setVisible(false);
                return;
            }
            @SuppressWarnings("unchecked")
            var items = ((List<TrackModel>) tracksTableView.getUserData()).stream()
                    .filter(m -> m.track().title().toLowerCase().contains(newInput))
                    .collect(Collectors.toCollection(ArrayList::new));
            LOG.info("Search by: '{}'. Found: {}", newInput, items.size());
            items.sort(Comparator.comparing(m -> m.track().title().toLowerCase()));
            tracksTableView.setItems(FXCollections.observableArrayList(items));
            tracksTableView.getSelectionModel().selectFirst();
            if (!tracksTableHeader.tracksSearchLabel().isVisible()) {
                tracksTableHeader.tracksSearchLabel().setVisible(true);
            }
            tracksTableHeader.tracksSearchLabel().setText(newInput);
        };
    }

    private Runnable onSearchTextInputCleared() {
        return () -> {
            @SuppressWarnings("unchecked")
            var sourceItems = (List<TrackModel>) tracksTableView.getUserData();
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

    @FXML
    private void showConfigColumnsDialog() {
        var availableColumns = new ArrayList<String>();
        var visibleColumns = new ArrayList<String>();
        tracksTableView().getColumns().forEach(col -> {
            var colName = col.getText();
            if (col.isVisible()) {
                visibleColumns.add(colName);
            } else {
                availableColumns.add(colName);
            }
        });
        new TracksTableViewColumnsDialog(availableColumns, visibleColumns).afterShowAndWait(selectedColumns -> {
            tracksTableView().getColumns().forEach(col -> col.setVisible(selectedColumns.contains(col.getText())));
        });
    }
}
