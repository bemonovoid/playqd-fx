package io.playqd.mini.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.playqd.data.Track;
import io.playqd.event.MouseEventHelper;
import io.playqd.mini.controller.configurer.ItemsViewConfigurerFactory;
import io.playqd.mini.controller.item.LibraryItemRow;
import io.playqd.mini.controller.item.QueuedTrackItemRow;
import io.playqd.mini.controller.item.TrackItemRow;
import io.playqd.mini.controller.navigator.ItemsNavigator;
import io.playqd.mini.controller.navigator.NavigableItems;
import io.playqd.mini.custom.ConfirmDeleteRowItemsDialog;
import io.playqd.player.Player;
import io.playqd.service.MusicLibrary;
import io.playqd.utils.ClipboardHelper;

public class MiniLibraryItemsViewController {

    private static final Logger LOG = LoggerFactory.getLogger(MiniLibraryItemsViewController.class);

    private final ItemsNavigator itemsNavigator = new ItemsNavigator(this::onItemsNavigationChanged);

    @FXML
    private ToolBar itemsTableHeaderToolBar;

    @FXML
    private HBox headerLeft, headerCenter, headerRight, footerContainer;

    @FXML
    private MenuButton showHistoryMenuBtn, viewOptionsMenuBtn;

    @FXML
    private Button moveBackBtn, moveForwardBtn;

    @FXML
    private TableView<LibraryItemRow> tableView;

    @FXML
    private Label itemPathFooterLabel, footerLabel;

    @FXML
    private void initialize() {
        initTableHeaderProperties();
        initTableFooterProperties();
        initItemsHistoryControls();
        initViewOptionsControls();
        initTableProperties();
        initRowFactories();
        initKeyPressedHandlers();
        initLibraryEventHandlers();
        initPlayerEventHandlers();
    }

    private void initTableHeaderProperties() {
        itemsTableHeaderToolBar.setMinWidth(Region.USE_PREF_SIZE);
        StackPane.setAlignment(headerLeft, Pos.CENTER_LEFT);
        StackPane.setAlignment(headerCenter, Pos.CENTER);
        StackPane.setAlignment(headerRight, Pos.CENTER_RIGHT);
    }

    private void initTableFooterProperties() {
        itemPathFooterLabel.prefWidthProperty().bind(footerContainer.widthProperty().multiply(0.7));
        itemPathFooterLabel.maxWidthProperty().bind(footerContainer.widthProperty().multiply(0.7));


        var copyFullPath = new MenuItem("Copy path");
        var copyPathSegments = new Menu("Copy path segment");

        copyFullPath.setOnAction(_ ->
                ClipboardHelper.putString(itemsNavigator.getCurrentState().descriptor().path().value()));

        var copyPathContextMenu = new ContextMenu();

        copyPathContextMenu.getItems().addAll(copyFullPath, copyPathSegments);

        copyPathContextMenu.setOnShowing(_ -> {
            copyPathSegments.getItems().clear();
            var itemPath = itemsNavigator.getCurrentState().descriptor().path();

            if (!itemPath.pathVariables().isEmpty()) {
                var copySegmentMenuItems = itemPath.pathVariables().stream()
                        .map(pathVariable -> {
                            var copySegmentMenuItem = new MenuItem(pathVariable);
                            copySegmentMenuItem.setOnAction(_ -> ClipboardHelper.putString(pathVariable));
                            return copySegmentMenuItem;
                        })
                        .toList();
                copyPathSegments.getItems().addAll(copySegmentMenuItems);
            }
        });
        itemPathFooterLabel.setContextMenu(copyPathContextMenu);

    }

    private void initItemsHistoryControls() {
        showHistoryMenuBtn.getStyleClass().setAll("button", "icon-button");
        showHistoryMenuBtn.setOnShowing(_ -> {
            showHistoryMenuBtn.getItems().clear();
            var navItems = itemsNavigator.getReadOnlyNavigableItems();
            var menuItems = new ArrayList<MenuItem>(navItems.size());
            for (int i = 0; i < navItems.size(); i++) {
                var menuItem = new MenuItem(navItems.get(i).descriptor().path().value());
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

    private void initViewOptionsControls() {
        viewOptionsMenuBtn.getStyleClass().setAll("icon-button", "button");
        viewOptionsMenuBtn.setOnShowing(_ -> {
            viewOptionsMenuBtn.getItems().clear();
            var configurer = ItemsViewConfigurerFactory.get(itemsNavigator.getCurrentState().type(), this);
            var items = configurer.configureViewOptionsMenuItems(tableView);
            viewOptionsMenuBtn.getItems().addAll(items.get());
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
                        ItemsViewConfigurerFactory.get(row.getItem().getClass(), this).onOpen(tableView);
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
            if (keyEvent.isShortcutDown()) {
                if (keyEvent.isControlDown()) {
                    if (keyCode == KeyCode.LEFT) {
                        moveBack();
                    } else if (keyCode == KeyCode.RIGHT) {
                        moveForward();
                    }
                }
            } else if (keyCode == KeyCode.BACK_SPACE) {
                moveBack(); //TODO move to foldersview
            } else if (keyCode == KeyCode.ENTER) {
                var selectedItems = tableView.getSelectionModel().getSelectedItems();
                ItemsViewConfigurerFactory.get(selectedItems.getFirst().getClass(), this).onOpen(tableView);
                keyEvent.consume();
            } else if (keyCode == KeyCode.DELETE || keyCode == KeyCode.F8) {
                var selectedItems = tableView.getSelectionModel().getSelectedItems();
                var onItemsDeleteOpt =
                        ItemsViewConfigurerFactory.get(selectedItems.getFirst().getClass(), this).onDelete();
                onItemsDeleteOpt.ifPresent(itemsConsumer -> {
                    var confirmed = ConfirmDeleteRowItemsDialog.confirmDelete(selectedItems);
                    if (confirmed) {
                        itemsConsumer.andThen(_ -> refreshLastState()).accept(selectedItems);
                    }
                });
                keyEvent.consume();
            }
        });
    }

    private void initLibraryEventHandlers() {
        MusicLibrary.updatedTracksProperty().addListener((_, _, newValue) -> {
            if (newValue != null && newValue.tracks() != null && !newValue.tracks().isEmpty()) {
                Platform.runLater(() -> {
                    if (!tableView.getItems().isEmpty() && tableView.getItems().getFirst() instanceof TrackItemRow) {
                        var updatedTracks = newValue.tracks().stream().collect(Collectors.toMap(Track::id, t -> t));
                        for (int i = 0; i < tableView.getItems().size(); i++) {
                            var trackRowItem = (TrackItemRow) tableView.getItems().get(i);
                            var updatedTrack = updatedTracks.get(trackRowItem.getId());
                            if (updatedTrack != null) {
                                trackRowItem.setTrack(updatedTrack);
                                // This will trigger cell update and reset the status icons
                                trackRowItem.setStatus(UUID.randomUUID().toString());
                            }
                        }
                    }
                });
            }
        });
    }

    private void initPlayerEventHandlers() {
        Player.onPlayingTrackChanged(track -> {
            Platform.runLater(() -> {
                if (!tableView.getItems().isEmpty()
                        && tableView.getItems().getFirst() instanceof QueuedTrackItemRow queuedTrackItemRow) {
                    var selectedIdx = tableView.getSelectionModel().getSelectedIndex();
                    var playingTrackIdx = -1;
                    for (int i = 0; i < tableView.getItems().size(); i++) {
                        if (tableView.getItems().get(i).getId() == track.id()) {
                            playingTrackIdx = i;
                            break;
                        }
                    }
                    if (selectedIdx != playingTrackIdx) {
                        tableView.getSelectionModel().clearSelection();
                        tableView.getSelectionModel().select(playingTrackIdx);
                        tableView.scrollTo(playingTrackIdx);
                    }
                }
            });
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
        if (itemsNavigator.getCurrentState() != null) {
            var path = itemsNavigator.getCurrentState().descriptor().path();
            itemPathFooterLabel.setText(path.value());
            itemPathFooterLabel.setTooltip(new Tooltip(path.value()));
        }
    }

    public void refreshLastState() {
        showItems(itemsNavigator.getCurrentState(), false);
    }

    public void showItems(NavigableItems navigableItems) {
        showItems(navigableItems, navigableItems.descriptor().isPresent());
    }

    public void showItems(NavigableItems navigableItems, Predicate<LibraryItemRow> selectItemIf) {
        showItems(navigableItems, navigableItems.descriptor().isPresent(), selectItemIf);
    }

    private void showItems(NavigableItems navigableItems, boolean saveState) {
        LOG.info("Showing items for path: {}", navigableItems.descriptor().path());
        showItems(navigableItems, saveState, null);
    }

    private void showItems(NavigableItems navigableItems, boolean saveState, Predicate<LibraryItemRow> selectItemIf) {
        LOG.info("Showing items for path: {}", navigableItems.descriptor().path());
        showNewItems(navigableItems, saveState, selectItemIf);
    }

    private void showNewItems(NavigableItems navigableItems, boolean saveState, Predicate<LibraryItemRow> selectItemIf) {
        Platform.runLater(() -> {
            var configurer = ItemsViewConfigurerFactory.get(navigableItems.type(), this);
            configurer.configureColumns(tableView);
            configurer.configureHeader(navigableItems.descriptor(), tableView, headerLeft, headerRight);
            if (saveState) {
                itemsNavigator.addState(navigableItems);
            }
            tableView.getSelectionModel().clearSelection();
            tableView.getItems().clear();
            var items = navigableItems.supplier().get();
            if (!items.isEmpty()) {
                setTableViewItems(items, selectItemIf);
            }
            configurer.configureFooter(tableView, footerLabel);
        });
    }

    private void setTableViewItems(List<LibraryItemRow> items, Predicate<LibraryItemRow> selectItemIf) {
        tableView.setItems(FXCollections.observableArrayList(items));
        if (!items.isEmpty()) {
            tableView.requestFocus();
            var selectedIdx = 0;
            if (selectItemIf != null) {
                for (int i = 0; i < tableView.getItems().size(); i++) {
                    if (selectItemIf.test(tableView.getItems().get(i))) {
                        tableView.getSelectionModel().clearSelection();
                        selectedIdx = i;
                        break;
                    }
                }
            }
            tableView.scrollTo(selectedIdx);
            tableView.getSelectionModel().select(selectedIdx);
        }
    }
}

