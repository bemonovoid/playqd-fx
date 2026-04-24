package io.playqd.mini.controller;

public record SearchToken(String value, SearchFilter filterType) {

    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }
}
