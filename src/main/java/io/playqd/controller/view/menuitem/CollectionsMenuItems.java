package io.playqd.controller.view.menuitem;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.controller.collections.CollectionDialog;
import io.playqd.data.NewMediaCollectionItem;
import io.playqd.service.MusicLibrary;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class CollectionsMenuItems implements MenuItemsBuilder {

    private Supplier<List<NewMediaCollectionItem>> itemsSupplier = Collections::emptyList;

    public CollectionsMenuItems onAddItemsToCollection(Supplier<List<NewMediaCollectionItem>> itemsSupplier) {
        this.itemsSupplier = itemsSupplier;
        return this;
    }

    @Override
    public List<MenuItem> build() {
        var menu = new Menu("Add to collection", new FontAwesomeIconView(FontAwesomeIcon.CLONE));
        menu.getItems().add(createAddToNewCollectionMenuItem());
        menu.getItems().addAll(createCollectionMenuItems());
        return List.of(menu);
    }

    private MenuItem createAddToNewCollectionMenuItem() {
        var menuItem = new MenuItem("<<New collection>>");
        menuItem.setOnAction(_ -> {
            var dialog = new CollectionDialog();
            dialog.showAndWait().ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    if (itemsSupplier != null) {
                        var itemsToAdd = itemsSupplier.get();
                        if (itemsToAdd != null && !itemsToAdd.isEmpty()) {
                            MusicLibrary.createCollection(name, itemsToAdd);
                        }
                    }
                }
            });
        });
        return menuItem;
    }

    private List<MenuItem> createCollectionMenuItems() {
        return MusicLibrary.getCollections().stream()
                .map(c -> {
                    var menuItem = new MenuItem(c.name());
                    menuItem.setUserData(c);
                    if (itemsSupplier != null) {
                        var itemsToAdd = itemsSupplier.get();
                        if (itemsToAdd != null && !itemsToAdd.isEmpty()) {
                            menuItem.setOnAction(_ -> MusicLibrary.addItemsToCollection(c.id(), itemsToAdd));
                        }
                    }
                    return menuItem;
                })
                .toList();
    }
}
