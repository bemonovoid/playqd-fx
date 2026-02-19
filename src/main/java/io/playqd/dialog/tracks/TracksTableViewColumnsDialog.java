package io.playqd.dialog.tracks;

import io.playqd.dialog.DialogOptions;
import io.playqd.dialog.PlayqdDialog;
import io.playqd.dialog.PlayqdDialogPane;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import javafx.scene.control.DialogPane;

import java.util.List;
import java.util.Set;

public class TracksTableViewColumnsDialog extends PlayqdDialog<Set<String>> {

    public TracksTableViewColumnsDialog(List<String> availableColumns, List<String> selectedColumns) {
        super(DialogOptions.builder().hideOnCloseRequest(true).build());
        setHeaderText("Configure columns");
        var dialogPane = new ThisDialogPane(availableColumns, selectedColumns);
        dialogPane.setDialog(this);
        setDialogPane(dialogPane);
    }

    public static final class ThisDialogPane
            extends PlayqdDialogPane<TracksTableViewColumnsDialog, TracksTableViewColumnsDialogController> {

        public ThisDialogPane(List<String> availableColumns, List<String> selectedColumns) {
            var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.DIALOG_TRACKS_TABLE_VIEW_COLUMNS);
            resourceLoader.setRoot(this);
            FXMLLoaderUtils.loadObject(resourceLoader, DialogPane.class);
            var controller = FXMLLoaderUtils.getController(resourceLoader, TracksTableViewColumnsDialogController.class);
            setController(controller);
            controller.setItems(availableColumns, selectedColumns);
        }

    }
}
