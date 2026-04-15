package io.playqd.mini.events;

import io.playqd.mini.controller.navigator.NavigableItems;

public final class NavigationEvent extends ApplicationEvent {

    private final NavigableItems navigableItems;

    public NavigationEvent(NavigableItems navigableItems) {
        super(NAVIGATION);
        this.navigableItems = navigableItems;
    }

    public NavigableItems getNavigableItems() {
        return navigableItems;
    }
}
