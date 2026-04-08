package io.playqd.controller.search;

import javafx.scene.Node;

import java.util.function.Consumer;

public interface Searchable {

    void initialize(Node keyEventNode, Consumer<String> resultConsumer, Runnable onCleared);
}
