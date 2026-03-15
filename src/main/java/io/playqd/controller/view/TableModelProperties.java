package io.playqd.controller.view;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public interface TableModelProperties {

    IntegerProperty getPlayCount();

    IntegerProperty getRating();

    StringProperty getRatedDisplayDate();

    StringProperty getLastPlayedDisplayDate();


}
