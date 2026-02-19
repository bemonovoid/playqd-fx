package io.playqd.controller.music;

import java.util.function.Consumer;

public interface Searchable {

    Consumer<String> onSearchTextInputChanged();

    Runnable onSearchTextInputCleared();
}
