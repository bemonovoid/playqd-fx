package io.playqd.controller.trackexplorer;

import javafx.beans.property.IntegerProperty;

public record ListItem(ListItemId id, String title, IntegerProperty countProperty) {

}
