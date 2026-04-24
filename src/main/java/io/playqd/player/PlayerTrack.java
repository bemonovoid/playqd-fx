package io.playqd.player;

import java.util.function.Supplier;

import io.playqd.data.Track;

public record PlayerTrack(Track track, Supplier<PlayerTrackStatus> status) {

}
