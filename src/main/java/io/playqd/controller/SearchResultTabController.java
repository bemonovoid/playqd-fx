package io.playqd.controller;

import io.playqd.client.GetTracksResponse;
import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;

public abstract class SearchResultTabController<T, R> {

    @FXML
    protected TableView<T> tableView;

    @FXML
    protected Pagination pagination;

    protected abstract void initTableInternal();

    protected abstract void initTablePagination();

    protected abstract void setTableItems(R response);

    protected void initializeInternal() {
        initDefaultTable();
        initTableInternal();
        initTablePagination();
    }

    private void initDefaultTable() {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }
}
