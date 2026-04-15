package io.playqd.mini.custom;

import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import io.playqd.mini.controller.NavigableItemsResolver;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.events.NavigationEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

import java.util.function.Supplier;

public class AlbumsHeaderMenuButton extends MenuButton {

    private final Supplier<LibraryItemRow> libraryItemRow;

    @FXML
    private ToggleGroup checkBoxGroup;

    @FXML
    private RadioMenuItem showAllAlbumsToggle, showAllTracksToggle;

    public AlbumsHeaderMenuButton(String selectedToggleId, Supplier<LibraryItemRow> libraryItemRow) {
        var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.ALBUMS_HEADER_MENU_BUTTON);
        resourceLoader.setRoot(this);
        resourceLoader.setController(this);
        FXMLLoaderUtils.loadObject(resourceLoader, AlbumsHeaderMenuButton.class);
        this.libraryItemRow = libraryItemRow;
        setSelectedToggle(selectedToggleId);
    }

    @FXML
    private void initialize() {
        getStyleClass().setAll("icon-button", "button");
    }

    @FXML
    private void showAllAlbums() {
        this.fireEvent(new NavigationEvent(NavigableItemsResolver.resolveArtistAlbums(libraryItemRow.get())));
    }

    @FXML
    private void showAllTracks() {
        this.fireEvent(new NavigationEvent(NavigableItemsResolver.resolveArtistTracks(libraryItemRow.get())));
    }

    private void setSelectedToggle(String toggleId) {
        checkBoxGroup.getToggles().forEach(toggle -> {
            if (toggle == showAllAlbumsToggle && showAllAlbumsToggle.getId().equals(toggleId)) {
                toggle.setSelected(true);
            } else if (toggle == showAllTracksToggle && showAllTracksToggle.getId().equals(toggleId)) {
                toggle.setSelected(true);
            }
        });
    }
}
