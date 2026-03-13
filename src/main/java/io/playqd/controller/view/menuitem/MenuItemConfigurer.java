package io.playqd.controller.view.menuitem;

import io.playqd.data.Track;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MenuItemConfigurer {

    private final Supplier<Node> icon;

    private String text;
    private Consumer<List<Track>> onAction;
    private boolean excluded = false;

    public MenuItemConfigurer(String text) {
        this(text, () -> null);
    }

    public MenuItemConfigurer(String text, Supplier<Node> icon) {
        this(text, icon, (_) -> {});
    }

    public MenuItemConfigurer(String text, Consumer<List<Track>> onAction) {
        this(text, () -> null, onAction);
    }

    public MenuItemConfigurer(String text, Supplier<Node> icon, Consumer<List<Track>> onAction) {
        this.text = text;
        this.icon = icon;
        this.onAction = onAction;
    }

    public String text() {
        return text;
    }

    public Supplier<Node> icon() {
        return icon;
    }

    public boolean isExcluded() {
        return excluded;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setOnAction(Consumer<List<Track>> onAction) {
        this.onAction = onAction;
    }

    public void setExcluded(boolean excluded) {
        this.excluded = excluded;
    }

    public MenuItem configure(List<Track> selectedTracks) {
        if (excluded) {
            return null;
        }
        var menuItem = new MenuItem(this.text, this.icon.get());
        menuItem.setOnAction(_ -> onAction.accept(selectedTracks));
        return menuItem;
    }
}
