package io.playqd.controller.view.menuitem;

import io.playqd.data.Track;
import javafx.scene.Node;
import javafx.scene.control.Menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class MenuConfigurer extends MenuItemConfigurer {

    private final List<MenuItemConfigurer> menuItemConfigurers;

    public MenuConfigurer(String text, List<MenuItemConfigurer> menuItemConfigurers) {
        this(text, () -> null, menuItemConfigurers);
    }

    public MenuConfigurer(String text,
                          Supplier<Node> icon,
                          List<MenuItemConfigurer> menuItemConfigurers) {
        super(text, icon, null);
        this.menuItemConfigurers = new ArrayList<>(menuItemConfigurers);
    }

    public List<MenuItemConfigurer> itemsConfigurers() {
        return menuItemConfigurers;
    }

    public void addMenuItemConfigurers(List<MenuItemConfigurer> configurers) {
        menuItemConfigurers.addAll(configurers);
    }

    @Override
    public Menu configure(List<Track> selectedTracks) {
        if (isExcluded()) {
            return null;
        }
        var menu = new Menu(text());
        menu.setGraphic(icon().get());
        if (menuItemConfigurers != null && !menuItemConfigurers.isEmpty()) {
            var items = menuItemConfigurers.stream()
                    .map(c -> c.configure(selectedTracks))
                    .filter(Objects::nonNull)
                    .toList();
            menu.getItems().addAll(items);
        }
        return menu;
    }
}
