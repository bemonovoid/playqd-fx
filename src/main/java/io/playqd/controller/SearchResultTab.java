package io.playqd.controller;

import io.playqd.client.PageableResponse;
import io.playqd.data.SearchFlag;
import javafx.scene.control.Tab;

import java.util.function.BiConsumer;

public abstract class SearchResultTab<T extends PageableResponse> extends Tab {

    abstract void setItems(T response);

    abstract void onPageChanged(BiConsumer<SearchFlag, Integer> consumer);
}
