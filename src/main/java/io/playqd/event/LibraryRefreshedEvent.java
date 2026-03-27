package io.playqd.event;

import javafx.event.EventType;

public final class LibraryRefreshedEvent extends PlayqdEvent {

    public static final EventType<LibraryRefreshedEvent> LIBRARY_REFRESHED_EVENT =
            new EventType<>("LIBRARY_REFRESHED_EVENT");

    public LibraryRefreshedEvent() {
        super(LIBRARY_REFRESHED_EVENT);
    }
}
