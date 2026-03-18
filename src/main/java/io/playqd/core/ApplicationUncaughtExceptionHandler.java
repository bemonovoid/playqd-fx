package io.playqd.core;

import io.playqd.data.PlayqdException;
import org.controlsfx.dialog.ExceptionDialog;

public final class ApplicationUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable throwable) {
        var cause = throwable;
        while (cause != null) {
            if (cause instanceof PlayqdException) {
                break;
            } else {
                cause = cause.getCause();
            }
        }
        new ExceptionDialog(cause != null ? cause : throwable).showAndWait();
    }
}
