package io.playqd.mini.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.playqd.event.MouseEventHelper;
import io.playqd.mini.controller.configurer.ItemsViewConfigurerFactory;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.navigator.ItemsNavigator;
import io.playqd.mini.controller.navigator.NavigableItems;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

public class MiniLibraryItemsViewController {

    private final ItemsNavigator itemsNavigator = new ItemsNavigator(this::onItemsNavigationChanged);

    @FXML
    private ToolBar headerToolBar;

    @FXML
    private HBox headerLeft, headerCenter, headerRight;

    @FXML
    private MenuButton showHistoryMenuBtn;

    @FXML
    private Button moveBackBtn, moveForwardBtn;

    @FXML
    private TableView<LibraryItemRow> tableView;

    @FXML
    private Label footerLabel;

    @FXML
    private void initialize() {
        initTableHeaderProperties();
        initItemsHistoryControls();
        initTableProperties();
        initRowFactories();
    }

    private void initTableHeaderProperties() {
        headerToolBar.setMinWidth(Region.USE_PREF_SIZE);
        StackPane.setAlignment(headerLeft, Pos.CENTER_LEFT);
        StackPane.setAlignment(headerCenter, Pos.CENTER);
        StackPane.setAlignment(headerRight, Pos.CENTER_RIGHT);
    }

    private void initItemsHistoryControls() {
        showHistoryMenuBtn.getStyleClass().setAll("icon-button", "button");
        showHistoryMenuBtn.setOnShowing(_ -> {
            showHistoryMenuBtn.getItems().clear();
            var navItems = itemsNavigator.getReadOnlyNavigableItems();
            var menuItems = new ArrayList<MenuItem>(navItems.size());
            for (int i = 0; i < navItems.size(); i++) {
                var menuItem = new MenuItem(navItems.get(i).descriptor().get());
                if (itemsNavigator.getCurrentIndex() == i) {
                    menuItem.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CARET_RIGHT));
                }
                var moveTo = i;
                menuItem.setOnAction(_ -> {
                    var navItem = itemsNavigator.moveTo(moveTo);
                    showItems(navItem, false);
                });
                menuItems.add(menuItem);
            }

            var clearHistoryMenuItem = new MenuItem("Clear history");
            clearHistoryMenuItem.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.REMOVE));

            showHistoryMenuBtn.getItems().addAll(clearHistoryMenuItem, new SeparatorMenuItem());
            showHistoryMenuBtn.getItems().addAll(menuItems.reversed());
        });
    }

    private void initTableProperties() {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private void initRowFactories() {
        tableView.setRowFactory(_ -> {
            var row = new TableRow<LibraryItemRow>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty()) {
                    if (MouseEventHelper.primaryButtonDoubleClicked(e)) {
                        executeOnItemMouseDoubleClicked(row);
                    } else if (MouseEventHelper.secondaryButtonSingleClicked(e)) {
                        executeOnItemMouseSecondaryClicked(row);
                    }
                }
            });
            return row;
        });
    }

    private void executeOnItemMouseDoubleClicked(TableRow<LibraryItemRow> row) {
        if (!row.isEmpty()) {
            var configurer = ItemsViewConfigurerFactory.get(row.getItem().getClass(), this);
            configurer.onItemMouseDoubleClicked(row.getItem());
        }
    }

    private void executeOnItemMouseSecondaryClicked(TableRow<LibraryItemRow> row) {
        //TODO context menu
    }

    @FXML
    private void moveBack() {
        if (itemsNavigator.canGoBack()) {
            showItems(itemsNavigator.moveBack(), false);
            onItemsNavigationChanged();
        }
    }

    @FXML
    private void moveForward() {
        if (itemsNavigator.canGoForward()) {
            showItems(itemsNavigator.moveForward(), false);
            onItemsNavigationChanged();
        }
    }

    private void onItemsNavigationChanged() {
        moveBackBtn.setDisable(!itemsNavigator.canGoBack());
        moveForwardBtn.setDisable(!itemsNavigator.canGoForward());
        showHistoryMenuBtn.setDisable(!itemsNavigator.canGoBack() && !itemsNavigator.canGoForward());
    }

    public void refreshLastState() {
        showItems(itemsNavigator.getCurrentState(), false);
    }

    public void showItems(NavigableItems navigableItems) {
        showItems(navigableItems, navigableItems.descriptor().isPresent());
    }

    private void showItems(NavigableItems navigableItems, boolean saveState) {
        tableView.getItems().clear();
        Platform.runLater(() -> {
            var configurer = ItemsViewConfigurerFactory.get(navigableItems.type(), this);
            configurer.configureColumns(tableView);
            configurer.configureHeader(tableView, headerLeft, headerRight);
            if (saveState) {
                itemsNavigator.addState(navigableItems);
            }
            var items = navigableItems.supplier().get();
            if (!items.isEmpty()) {
                setTableViewItems(items);
            }
            configurer.configureFooter(tableView, footerLabel);
        });
    }

    private void setTableViewItems(List<LibraryItemRow> items) {
        tableView.setItems(FXCollections.observableArrayList(items));
        if (!items.isEmpty()) {
            tableView.scrollTo(0);
        }
    }
}

