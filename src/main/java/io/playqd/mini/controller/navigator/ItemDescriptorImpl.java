package io.playqd.mini.controller.navigator;

import io.playqd.mini.controller.item.LibraryItemRow;

record ItemDescriptorImpl(String path, LibraryItemRow parent) implements ItemsDescriptor {

}
