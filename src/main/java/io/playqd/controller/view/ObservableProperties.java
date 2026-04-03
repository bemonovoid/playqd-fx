package io.playqd.controller.view;

import io.playqd.controller.view.request.ApplicationViewRequest;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class ObservableProperties {

    private final static ObjectProperty<ApplicationViewRequest> APP_VIEW_REQUEST_PROPERTY =
            new SimpleObjectProperty<>();

    public static ReadOnlyObjectProperty<ApplicationViewRequest> getAppViewRequestProperty() {
        return APP_VIEW_REQUEST_PROPERTY;
    }

    public static void setAppViewRequestProperty(ApplicationViewRequest appViewRequest) {
        APP_VIEW_REQUEST_PROPERTY.set(null);
        APP_VIEW_REQUEST_PROPERTY.set(appViewRequest);
    }

}
