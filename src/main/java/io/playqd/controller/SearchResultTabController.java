package io.playqd.controller;

import com.sun.jna.NativeLibrary;
import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import uk.co.caprica.vlcj.binding.support.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.factory.discovery.strategy.LinuxNativeDiscoveryStrategy;
import uk.co.caprica.vlcj.player.base.MediaPlayer;

public abstract class SearchResultTabController<T, R> {

    protected static final int DEFAULT_MAX_PAGE_INDICATOR_COUNT = 10;

    @FXML
    protected TableView<T> tableView;

    @FXML
    protected Pagination pagination;

    protected abstract void initTableInternal();

    protected abstract void setTableItems(R response);

    protected void initializeInternal() {
        initDefaultTable();
        initTableInternal();

        var discovery = new NativeDiscovery();
        var p = discovery.discoveredPath();

    }

    private void initDefaultTable() {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

}
