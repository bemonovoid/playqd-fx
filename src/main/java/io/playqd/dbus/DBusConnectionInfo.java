package io.playqd.dbus;

import org.freedesktop.dbus.connections.BusAddress;

public record DBusConnectionInfo(boolean connected,
                                 String identity,
                                 String machineId,
                                 String[] names,
                                 BusAddress address) {
}
