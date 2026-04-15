package io.playqd.mini.controller.navigator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ItemsNavigator {

    private int currentIndex = -1;
    private final List<NavigableItems> history = new ArrayList<>();

    private final Runnable onItemsNavigationChanged;

    public ItemsNavigator(Runnable onItemsNavigationChanged) {
        this.onItemsNavigationChanged = onItemsNavigationChanged;
    }

    public void addState(NavigableItems state) {
        history.add(state);
        currentIndex = history.size() - 1;
        onItemsNavigationChanged.run();
    }

    public NavigableItems moveBack() {
        if (currentIndex > 0) {
            currentIndex--;
        }
        return history.get(currentIndex);
    }

    public NavigableItems moveForward() {
        if (currentIndex < history.size() - 1) {
            currentIndex++;
        }
        return history.get(currentIndex);
    }

    public NavigableItems moveTo(int index) {
        return history.get(currentIndex = index);
    }

    public boolean canGoBack() {
        return currentIndex > 0;
    }

    public boolean canGoForward() {
        return currentIndex < history.size() - 1;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public NavigableItems getCurrentState() {
        if (isEmpty()) {
            return null;
        }
        return history.get(currentIndex);
    }

    public boolean isEmpty() {
        return currentIndex < 0 || history.isEmpty();
    }

    public List<NavigableItems> getReadOnlyNavigableItems() {
        return Collections.unmodifiableList(history);
    }

    public void clearAllButCurrent() {
        var current = getCurrentState();
        history.clear();
        addState(current);
        onItemsNavigationChanged.run();
    }
}
