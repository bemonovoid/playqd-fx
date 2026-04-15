package io.playqd.mini.controller.configurer;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.data.Track;
import io.playqd.mini.controller.ItemsTableColumnIds;
import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.NavigableItemsResolver;
import io.playqd.mini.controller.factories.*;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.TrackItemRow;
import io.playqd.mini.controller.item.contextmenu.ContextMenuItemsBuilder;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
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
import java.util.List;

public sealed class TracksViewConfigurer extends DefaultItemsViewConfigurer permits
        ArtistTracksViewConfigurer,
        AlbumTracksViewConfigurer,
        QueuedTracksViewConfigurer,
        PlaylistTracksViewConfigurer {

    private final static Logger LOG = LoggerFactory.getLogger(TracksViewConfigurer.class);

    public TracksViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    public void configureColumns(TableView<LibraryItemRow> tableView) {
        super.configureColumns(tableView);
        tableView.getColumns()
                .forEach(col -> {
                    if (col.getId().equals(ItemsTableColumnIds.MISC_VALUE_COL)) {
                        col.setVisible(true);
                        @SuppressWarnings("unchecked")
                        var miscValueCol = (TableColumn<LibraryItemRow, String>) col;
                        miscValueCol.setCellValueFactory(c -> c.getValue().getMiscValue());
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
    public void onRowOpened(LibraryItemRow item) {
        if (item instanceof TrackItemRow trackItemRow) {
            PlayerTrackListManager.enqueueAndPlay(new TrackListRequest(trackItemRow.getSource()));
        } else {
            LOG.error("Unexpected item type: {}. Expected type: {}", item.getClass(),  TrackItemRow.class);
        }
    }

    @Override
    public List<MenuItem> configureContextMenuItems(List<LibraryItemRow> selectedItems) {
        var items = selectedItems.stream().map(i -> (Track) i.getSource()).toList();
        return ContextMenuItemsBuilder.newBuilder()
                .playMenuItems(items)
                .playlistMenuItems(items)
                .build();
    }

    @Override
    protected ImageTableCellFactory geImageTableCellFactory() {
        return new TrackImageTableCellFactory();
    }

    @Override
    protected DescriptionTableCellFactory getDescriptionTableCellFactory() {
        return new HyperLinkTableCellFactory(NavigableItemsResolver::resolveArtistAlbums);
    }

    @Override
    protected MiscValueTableCellFactory getMiscValueTableCellFactory() {
        return null;
    }

    @Override
    protected void configureHeaderLeft(ItemsDescriptor itemsDescriptor, HBox headerLeft) {
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
