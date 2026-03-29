package io.playqd.controller.collections;

import io.playqd.data.MediaCollection;
import io.playqd.data.MediaCollectionItem;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;

public class CollectionItemsViewController {

    @FXML
    private Label titleLabel, lastModifiedDateLabel, selectedLabel, detailsLabel;

    @FXML
    private TableView<MediaCollectionItem> tableView;

    @FXML
    private TableColumn<MediaCollectionItem, String> iconCol, titleCol, itemTypeCol, commentCol;

    @FXML
    private void initialize() {
        initTableProperties();
        intiColumnCellFactories();
        initColumnCellValueFactories();
    }

    private void initTableProperties() {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private void intiColumnCellFactories() {
        iconCol.setCellFactory(new CollectionItemIconTableCellFactory());
    }

    private void initColumnCellValueFactories() {
        iconCol.setCellValueFactory(c -> new SimpleObjectProperty<>(""));
        titleCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().title()));
        itemTypeCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().itemType().name()));
        commentCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().comment()));
    }

    void showItems(MediaCollection collection) {
        updateTableHeader(collection);
        tableView.setUserData(collection.items());
        tableView.setItems(FXCollections.observableList(collection.items()));
        tableView.scrollTo(0);
    }

    private void updateTableHeader(MediaCollection collection) {
        titleLabel.setText(collection.name());
        lastModifiedDateLabel.setText(collection.lastModifiedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
}
