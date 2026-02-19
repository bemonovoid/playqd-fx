package io.playqd.controller.view;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import io.playqd.data.Artist;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Comparator;

public class ArtistsContainer extends VBox {

    @FXML
    private MenuButton sortArtistsMenuBtn;

    @FXML
    private ListView<Artist> artistsListView;

    public ArtistsContainer() {
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.ARTISTS_CONTAINER);
        resourceLoader.setRoot(this);
        resourceLoader.setController(this);
        FXMLLoaderUtils.loadObject(resourceLoader, ArtistsContainer.class);
    }

    @FXML
    public void initialize() {
        sortArtistsMenuBtn.getStyleClass().setAll("button");
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

        byNameAscMenuItem.setOnAction(_ -> sort(Comparator.comparing(Artist::name), false));
        byNameDescMenuItem.setOnAction(_ -> sort(Comparator.comparing(Artist::name), true));

        byAlbumCountAscMenuItem.setOnAction(_ -> sort(Comparator.comparing(Artist::albumsCount), false));
        byAlbumCountDescMenuItem.setOnAction(_ -> sort(Comparator.comparing(Artist::albumsCount), true));

        byTracksCountAscMenuItem.setOnAction(_ -> sort(Comparator.comparing(Artist::tracksCount), false));
        byTracksCountDescMenuItem.setOnAction(_ -> sort(Comparator.comparing(Artist::tracksCount), true));

        sortArtistsMenuBtn.getItems().addAll(byNameAscMenuItem, byNameDescMenuItem, new SeparatorMenuItem(),
                byAlbumCountAscMenuItem, byAlbumCountDescMenuItem, new SeparatorMenuItem(),
                byTracksCountAscMenuItem, byTracksCountDescMenuItem);
    }

    public ListView<Artist> getArtistsListView() {
        return artistsListView;
    }

    public Artist getSelectedArtist() {
        return getArtistsListView().getSelectionModel().getSelectedItem();
    }

    private void sort(Comparator<Artist> comparator, boolean reversed) {
        var allArtistsItem = artistsListView.getItems().getFirst();
        var artistItems = artistsListView.getItems().subList(1, artistsListView.getItems().size());
        if (reversed) {
            artistItems.sort(comparator.reversed());
        } else {
            artistItems.sort(comparator);
        }
        var sortedItems = new ArrayList<Artist>(artistsListView.getItems().size());
        sortedItems.add(allArtistsItem);
        sortedItems.addAll(artistItems);
        artistsListView.setItems(FXCollections.observableArrayList(sortedItems));
    }
}
