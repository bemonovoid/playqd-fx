package io.playqd.player;

import java.util.function.Consumer;

public interface PlayerEventConsumer<T> extends Consumer<T> {

    void accept(T data);

}
