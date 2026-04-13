package io.playqd.mini.controller.configurer;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.data.Track;
import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.factories.TrackImageTableCellFactory;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.TrackItemRow;
import io.playqd.player.PlayerTrackListManager;
import io.playqd.player.TrackListRequest;
import io.playqd.utils.TimeUtils;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.time.Duration;

public sealed class TracksItemsViewConfigurer extends DefaultItemsViewConfigurer
        permits AlbumTracksItemsViewConfigurer, QueuedTracksItemsViewConfigurer {

    private final static Logger LOG = LoggerFactory.getLogger(TracksItemsViewConfigurer.class);

    public TracksItemsViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    public void configureColumns(TableView<LibraryItemRow> tableView) {
        tableView.getColumns()
                .forEach(col -> {
                    if (col.getId().equals("imageCol")) {
                        @SuppressWarnings("unchecked")
                        var imageCol = (TableColumn<LibraryItemRow, Long>) col;
                        imageCol.setCellFactory(new TrackImageTableCellFactory());
                    } else if (col.getId().equals("miscValueCol")) {
                        col.setVisible(true);
                        col.setMinWidth(55);
                        col.setMaxWidth(55);
                        @SuppressWarnings("unchecked")
                        var miscValueCole = (TableColumn<LibraryItemRow, String>) col;
                        miscValueCole.setCellValueFactory(c -> c.getValue().getMiscValue());
                    }
                });
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
    public void onItemMouseDoubleClicked(LibraryItemRow item) {
        if (item instanceof TrackItemRow trackItemRow) {
            PlayerTrackListManager.enqueueAndPlay(new TrackListRequest(trackItemRow.getSource()));
        } else {
            LOG.error("Unexpected item type: {}. Expected type: {}", item.getClass(),  TrackItemRow.class);
        }
    }

    @Override
    protected void configureHeaderLeft(TableView<LibraryItemRow> tableView, HBox headerLeft) {
        headerLeft.getChildren().clear();
        headerLeft.getChildren().add(new Label("Tracks:"));
    }

    @Override
    protected void configureHeaderRight(TableView<LibraryItemRow> tableView, HBox headerRight) {
        headerRight.getChildren().addAll(createFilterMenuButton(), createSortMenuButton());
    }

    private Node createFilterMenuButton() {
        var playedMenuItem = new MenuItem("Played");
        playedMenuItem.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLAY));

        var likedMenuItem = new MenuItem("Liked");
        likedMenuItem.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.THUMBS_ALT_UP));

        var cueMenuItem = new MenuItem("Cue tracks");
        cueMenuItem.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE_AUDIO_ALT));

        var menuBtn = new MenuButton();

        menuBtn.setPadding(new Insets(1));
        menuBtn.setFocusTraversable(false);
        menuBtn.setAccessibleRole(AccessibleRole.BUTTON);
        menuBtn.getStyleClass().setAll("icon-button", "button");
        menuBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILTER));
        menuBtn.setPopupSide(Side.LEFT);

        menuBtn.getItems().addAll(playedMenuItem, likedMenuItem, cueMenuItem);

        return menuBtn;
    }

    private Node createSortMenuButton() {
        var titleMenuItem = new MenuItem("Title");
        titleMenuItem.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SORT_ALPHA_ASC));

        var lengthMenuItem = new MenuItem("Length");
        lengthMenuItem.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SORT_NUMERIC_ASC));

        var menuBtn = new MenuButton();

        menuBtn.setPadding(new Insets(1));
        menuBtn.setFocusTraversable(false);
        menuBtn.setAccessibleRole(AccessibleRole.BUTTON);
        menuBtn.getStyleClass().setAll("icon-button", "button");
        menuBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SORT_AMOUNT_DESC));
        menuBtn.setPopupSide(Side.LEFT);

        menuBtn.getItems().addAll(lengthMenuItem);

        return menuBtn;
    }
 }
