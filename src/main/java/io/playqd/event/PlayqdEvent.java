package io.playqd.event;

import io.playqd.data.SearchFlag;
import javafx.event.Event;
import javafx.event.EventType;

public class PlayqdEvent extends Event {

    public static final EventType<PlayqdEvent> PLAYQD_EVENTS = new EventType<>("PLAYQD_EVENTS");

    public static final EventType<SearchFlagChangedEvent> SEARCH_FLAG_CHANGED_EVENT =
            new EventType<>("SEARCH_FLAG_CHANGED_EVENT");


    public PlayqdEvent(EventType<? extends PlayqdEvent> eventType) {
        super(eventType);
    }

    public static final class SearchFlagChangedEvent extends PlayqdEvent {

        private final boolean enabled;
        private final SearchFlag searchFlag;

        public SearchFlagChangedEvent(SearchFlag searchFlag, boolean enabled) {
            super(SEARCH_FLAG_CHANGED_EVENT);
            this.enabled = enabled;
            this.searchFlag = searchFlag;
        }

        public SearchFlag searchFlag() {
            return searchFlag;
        }

        public boolean enabled() {
            return enabled;
        }
    }
}
