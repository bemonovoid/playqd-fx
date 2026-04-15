package io.playqd.mini.events;

import javafx.event.Event;
import javafx.event.EventType;

public class ApplicationEvent extends Event {

    public static final EventType<ApplicationEvent> MINI_PLAYER_APP_EVENTS = new EventType<>("MINI_PLAYER_APP_EVENTS");

    public static final EventType<NavigationEvent> NAVIGATION = new EventType<>("NAVIGATION_EVENT");

    public ApplicationEvent(EventType<? extends ApplicationEvent> eventType) {
        super(eventType);
    }
}
