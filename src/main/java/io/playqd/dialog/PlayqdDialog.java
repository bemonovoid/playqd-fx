package io.playqd.dialog;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public abstract class PlayqdDialog<R> extends Dialog<Object> {

    private static final Logger LOG = LoggerFactory.getLogger(PlayqdDialog.class);

    private final DialogOptions dialogOptions;

    public PlayqdDialog() {
        this(DialogOptions.builder().build());
    }

    public PlayqdDialog(String title) {
        this(DialogOptions.builder().build(), title);
    }

    public PlayqdDialog(DialogOptions dialogOptions) {
        this(dialogOptions, "");
    }

    public PlayqdDialog(DialogOptions dialogOptions, String title) {
        this.dialogOptions = dialogOptions;
        var dialogTitle = title == null || title.isEmpty() ? "Krypton" : title;
        setTitle(dialogTitle);
        initStyle(StageStyle.UNIFIED);

        setOnShowing(_ -> {
            if (dialogOptions.preventCloseOnEscKeyPressed()) {
                getDialogPane().addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
                    if (KeyCode.ESCAPE == keyEvent.getCode()) {
                        keyEvent.consume();
                        LOG.info("Escape was pressed but I am preventing it from closing.");
                    }
                });
            }

            var stage = (Stage) getDialogPane().getScene().getWindow();
//            stage.getIcons().add(IconResource.APPLICATION_ICON);

            onShowing();
        });

        setOnShown(_ -> {
            if (dialogOptions.hideOnCloseRequest()) {
                var window = getDialogPane().getScene().getWindow();
                window.setOnCloseRequest(_ -> window.hide());
            }
            onShown();
            LOG.info("Opened '{}' dialog.", getTitle());
        });

        setOnCloseRequest(_ -> {
            onClosing();
            LOG.info("'{}' dialog was closed.", getTitle());
        });
    }

    public final void afterShowAndWait(Consumer<R> resultHandler) {
        afterShowAndWait(resultHandler, bt -> LOG.info("Dialog exit on button '{}'", bt.getText()));
    }

    @SuppressWarnings("unchecked")
    public final void afterShowAndWait(Consumer<R> resultHandler, Consumer<ButtonType> buttonTypeHandler) {
        var showAndWaitResult = showAndWait();
        if (showAndWaitResult.isEmpty()) {
            return;
        }
        var res = showAndWaitResult.get();
        if (res instanceof ButtonType buttonType && buttonTypeHandler != null) {
            buttonTypeHandler.accept(buttonType);
            return;
        }
        try {
            resultHandler.accept((R) res);
        } catch (Exception e) {
           LOG.error(e.getMessage());
        }
    }

    protected void onShowing() {

    }

    protected void onShown() {

    }

    protected void onHiding() {

    }

    protected void onHidden() {

    }

    protected void onClosing() {

    }

    public static abstract class Error extends javafx.scene.control.Alert {

        public Error(String message) {
            super(AlertType.ERROR);
            setHeaderText(null);
            setGraphic(null);
            var content = new HBox();
//            content.getChildren().add(new Label(message, new ImageView(IconResource.ERROR_ICON)));
            getDialogPane().setContent(content);
            setIcon(this);
        }
    }

    private static void setIcon(Dialog<?> dialog) {
        dialog.showingProperty().addListener((_, _, isShowing) -> {
            if (isShowing) {
                var stage = (Stage) dialog.
                        getDialogPane().getScene().getWindow();
//                stage.getIcons().add(IconResource.APPLICATION_ICON);
            }
        });
    }

}
