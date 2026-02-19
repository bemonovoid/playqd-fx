package io.playqd.dialog.cache;

import io.playqd.dialog.DialogOptions;
import io.playqd.dialog.PlayqdDialog;
import io.playqd.dialog.PlayqdDialogPane;
import io.playqd.fxml.FXMLLoaderUtils;
import io.playqd.fxml.FXMLResource;
import javafx.scene.control.DialogPane;

public class InvalidateCacheDialog extends PlayqdDialog<Object> {

    public InvalidateCacheDialog() {
        super(DialogOptions.builder().hideOnCloseRequest(true).build());
        setTitle("Invalidate Caches");
        var dialogPane = new InvalidateCacheDialogPane();
        dialogPane.setDialog(this);
        setDialogPane(dialogPane);
    }

    static class InvalidateCacheDialogPane extends
            PlayqdDialogPane<InvalidateCacheDialog, InvalidateCacheDialogController> {

        public InvalidateCacheDialogPane() {
            var resourceLoader = FXMLLoaderUtils.resourceLoader(FXMLResource.DIALOG_INVALIDATE_CACHES);
            resourceLoader.setRoot(this);
            FXMLLoaderUtils.loadObject(resourceLoader, DialogPane.class);
            setController(FXMLLoaderUtils.getController(resourceLoader, InvalidateCacheDialogController.class));
        }
    }
}
