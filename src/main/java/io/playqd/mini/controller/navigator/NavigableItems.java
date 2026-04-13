package io.playqd.mini.controller.navigator;

import io.playqd.mini.controller.item.LibraryItemRow;

import java.util.List;
import java.util.function.Supplier;

public record NavigableItems(
        ItemsDescriptor descriptor,
        Supplier<List<LibraryItemRow>> supplier,
        Class<? extends LibraryItemRow> type) {

}
