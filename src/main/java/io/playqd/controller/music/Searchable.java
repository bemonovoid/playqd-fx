package io.playqd.controller.music;

import javafx.scene.Node;

import java.util.function.Consumer;

public interface Searchable {

    void initialize(Node keyEventNode, Consumer<String> resultConsumer, Runnable onCleared);
}
