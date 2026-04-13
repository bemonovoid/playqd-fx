package io.playqd.mini.controller.configurer;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.mini.controller.MiniLibraryItemsViewController;
import io.playqd.mini.controller.factories.AlbumImageTableCellFactory;
import io.playqd.mini.controller.item.AlbumItemRow;
import io.playqd.mini.controller.item.AlbumTrackItemRow;
import io.playqd.mini.controller.item.ArtistAlbumItemRow;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.navigator.ItemsDescriptor;
import io.playqd.mini.controller.navigator.NavigableItems;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public sealed class AlbumsItemsViewConfigurer extends DefaultItemsViewConfigurer permits ArtistAlbumsItemsViewConfigurer {

    private final static Logger LOG = LoggerFactory.getLogger(AlbumsItemsViewConfigurer.class);

    public AlbumsItemsViewConfigurer(MiniLibraryItemsViewController controller) {
        super(controller);
    }

    @Override
    public void configureColumns(TableView<LibraryItemRow> tableView) {
        tableView.getColumns()
                .forEach(col -> {
                    if (col.getId().equals("imageCol")) {
                        @SuppressWarnings("unchecked")
                        var imageCol = (TableColumn<LibraryItemRow, Long>) col;
                        imageCol.setCellFactory(new AlbumImageTableCellFactory());
                    }
                });
    }

    @Override
    public void configureFooter(TableView<LibraryItemRow> tableView, Label footerLabel) {
        var items = tableView.getItems();
        if (items.isEmpty()) {
            footerLabel.setText("");
        } else {
            footerLabel.setText(items.size() + " album" + (items.size() > 1 ? "s" : ""));
        }
    }

    @Override
    public void onItemMouseDoubleClicked(LibraryItemRow item) {
        if (item instanceof AlbumItemRow albumItemRow) {
            var album = albumItemRow.getSource();
            var descriptor = ItemsDescriptor.forAlbumTracks(album.artistName(), album.name());
            Supplier<List<LibraryItemRow>> supplier = () -> new ArrayList<>(
                    MusicLibrary.getAlbumTracks(item.getId()).stream().map(AlbumTrackItemRow::new).toList());
            controller.showItems(new NavigableItems(descriptor, supplier, AlbumTrackItemRow.class));
        }
    }

    @Override
    protected void configureHeaderLeft(TableView<LibraryItemRow> tableView, HBox headerLeft) {
        headerLeft.getChildren().add(new Label("Albums:"));
    }

    @Override
    protected void configureHeaderRight(TableView<LibraryItemRow> tableView, HBox headerRight) {
        var showAlbumsToggle = new ToggleButton();
        showAlbumsToggle.getStyleClass().addAll("icon-button", "icon-toggle-button");
        showAlbumsToggle.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FOLDER_ALT));

        var showArtistTracksToggle = new ToggleButton();
        showArtistTracksToggle.getStyleClass().addAll("icon-button", "icon-toggle-button");
        showArtistTracksToggle.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE_AUDIO_ALT));

        var toggleGroup = new ToggleGroup();

        showAlbumsToggle.setToggleGroup(toggleGroup);
        showArtistTracksToggle.setToggleGroup(toggleGroup);

        if (!tableView.getItems().isEmpty()) {
            var i = tableView.getItems().getFirst();
            if (i instanceof ArtistAlbumItemRow item) {

            } else if (i instanceof AlbumItemRow item) {

            }
        }
        headerRight.getChildren().addAll(showAlbumsToggle, showArtistTracksToggle);

    }
}
