package io.playqd.dialog.tracks;

import io.playqd.dialog.PlayqdAbstractDialogController;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import org.controlsfx.control.ListSelectionView;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class TracksTableViewColumnsDialogController extends
        PlayqdAbstractDialogController<TracksTableViewColumnsDialog.ThisDialogPane> {


    @FXML
    private ListSelectionView<String> columnSelectionView;

    @FXML
    private ButtonType okBtnType;

    @FXML
    private void initialize() {
        var okBtn = (Button) dialogPane.lookupButton(okBtnType);
        columnSelectionView.getTargetItems().addListener((ListChangeListener<String>) change -> {
            okBtn.setDisable(change.getList().isEmpty());
        });
        okBtn.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            dialogPane.setResult(Collections.unmodifiableSet(new HashSet<>(columnSelectionView.getTargetItems())));
        });
    }

    void setItems(List<String> availableColumns, List<String> selectedColumns) {
        columnSelectionView.getSourceItems().addAll(availableColumns);
        columnSelectionView.getTargetItems().addAll(selectedColumns);
    }

}
