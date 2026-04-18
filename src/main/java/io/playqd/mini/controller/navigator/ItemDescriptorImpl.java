package io.playqd.mini.controller.navigator;

import io.playqd.mini.controller.item.LibraryItemRow;

record ItemDescriptorImpl(ItemPath path, LibraryItemRow parent) implements ItemsDescriptor {

    ItemDescriptorImpl(String path) {
        this(path, null);
    }

    ItemDescriptorImpl(String path, LibraryItemRow parent) {
        this(new ItemPath(path), parent);
    }

}
