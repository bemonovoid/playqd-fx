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
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MiniLibraryItemsViewController {

    private static final Logger LOG = LoggerFactory.getLogger(MiniLibraryItemsViewController.class);

    private final ItemsNavigator itemsNavigator = new ItemsNavigator(this::onItemsNavigationChanged);

    @FXML
    private VBox miniLibraryItemsView;

    @FXML
    private ToolBar itemsTableHeaderToolBar;

    @FXML
    private HBox headerLeft, headerCenter, headerRight;

    @FXML
    private MenuButton showHistoryMenuBtn;

    @FXML
    private Button moveBackBtn, moveForwardBtn;

    @FXML
    private TableView<LibraryItemRow> tableView;

    @FXML
    private TableColumn<LibraryItemRow, String> nameCol, descriptionCol, miscValueCol;

    @FXML
    private Label footerLabel;

    @FXML
    private void initialize() {
        initTableHeaderProperties();
        initItemsHistoryControls();
        initTableProperties();
        initRowFactories();
        initKeyPressedHandlers();
    }

    private void initTableHeaderProperties() {
        itemsTableHeaderToolBar.setMinWidth(Region.USE_PREF_SIZE);
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
                var menuItem = new MenuItem(navItems.get(i).descriptor().path());
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
            clearHistoryMenuItem.setDisable(itemsNavigator.getReadOnlyNavigableItems().size() <= 1);
            clearHistoryMenuItem.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
            clearHistoryMenuItem.setOnAction(_ -> itemsNavigator.clearAllButCurrent());

            showHistoryMenuBtn.getItems().addAll(clearHistoryMenuItem, new SeparatorMenuItem());
            showHistoryMenuBtn.getItems().addAll(menuItems.reversed());
        });
    }

    private void initTableProperties() {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);
        nameCol.prefWidthProperty().bind(tableView.widthProperty().multiply(0.5));
        nameCol.maxWidthProperty().bind(tableView.widthProperty().multiply(0.5));
        descriptionCol.prefWidthProperty().bind(tableView.widthProperty().multiply(0.3));
        descriptionCol.maxWidthProperty().bind(tableView.widthProperty().multiply(0.3));
        miscValueCol.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));
        miscValueCol.maxWidthProperty().bind(tableView.widthProperty().multiply(0.2));
    }

    private void initRowFactories() {
        tableView.setRowFactory(_ -> {
            var row = new TableRow<LibraryItemRow>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty()) {
                    if (MouseEventHelper.primaryButtonDoubleClicked(e)) {
                        ItemsViewConfigurerFactory.get(row.getItem().getClass(), this).onRowOpened(row.getItem());
                    } else if (MouseEventHelper.secondaryButtonSingleClicked(e)) {
                        showContextMenu(e, row);
                    }
                }
            });
            return row;
        });
    }

    private void initKeyPressedHandlers() {
        initTableKeyPressedHandlers();
    }

    private void initTableKeyPressedHandlers() {
        tableView.setOnKeyPressed(keyEvent -> {
            var keyCode = keyEvent.getCode();
            if  (keyEvent.isShortcutDown()) {
                if (keyEvent.isControlDown()) {
                    if (keyCode == KeyCode.LEFT) {
                        moveBack();
                    } else if (keyCode == KeyCode.RIGHT) {
                        moveForward();
                    }
                }
            } else if (keyCode == KeyCode.ENTER) {
                var selectedItem = tableView.getSelectionModel().getSelectedItem();
                ItemsViewConfigurerFactory.get(selectedItem.getClass(), this).onRowOpened(selectedItem);
                keyEvent.consume();
            }
        });
    }

    private void showContextMenu(MouseEvent e, TableRow<LibraryItemRow> row) {
        if (row.getContextMenu() == null) {
            var configurer = ItemsViewConfigurerFactory.get(row.getItem().getClass(), this);
            var contextMenu = new ContextMenu();
            contextMenu.getItems().addAll(configurer.configureContextMenuItems(
                    tableView.getSelectionModel().getSelectedItems()));
            contextMenu.setOnHidden(_ -> row.setContextMenu(null)); // to reset a state
            row.setContextMenu(contextMenu);
            contextMenu.show(row, e.getScreenX(), e.getScreenY());
        }
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
        showHistoryMenuBtn.setDisable(itemsNavigator.getReadOnlyNavigableItems().isEmpty());
    }

    public void refreshLastState() {
        showItems(itemsNavigator.getCurrentState(), false);
    }

    public void showItems(NavigableItems navigableItems) {
        showItems(navigableItems, navigableItems.descriptor().isPresent());
    }

    private void showItems(NavigableItems navigableItems, boolean saveState) {
        LOG.info("Showing items for path: {}", navigableItems.descriptor().path());
        showNewItems(navigableItems, saveState);
    }

    private void showNewItems(NavigableItems navigableItems, boolean saveState) {
        Platform.runLater(() -> {
            var configurer = ItemsViewConfigurerFactory.get(navigableItems.type(), this);
            configurer.configureColumns(tableView);
            configurer.configureHeader(navigableItems.descriptor(), tableView, headerLeft, headerRight);
            if (saveState) {
                itemsNavigator.addState(navigableItems);
            }
            tableView.getItems().clear();
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
            tableView.requestFocus();
            tableView.scrollTo(0);
            tableView.getSelectionModel().selectFirst();
        }
    }
}

