package io.playqd.controller;

import io.playqd.client.PageableResponse;
import javafx.scene.control.Tab;

public abstract class SearchResultTab<T extends PageableResponse> extends Tab {

    abstract void setItems(T response);
}
