package io.playqd.dbus;

public interface ReturnableTypeRunnable<S, T> {
    S run(T value);
}
