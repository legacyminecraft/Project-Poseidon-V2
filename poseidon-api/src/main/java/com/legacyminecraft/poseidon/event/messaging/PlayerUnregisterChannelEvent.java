package com.legacyminecraft.poseidon.event.messaging;

import com.legacyminecraft.poseidon.network.connection.PlayerConnection;

/**
 * This event is fired when a {@link PlayerConnection} unregisters for a plugin
 * channel.
 */
public class PlayerUnregisterChannelEvent extends PlayerChannelEvent {

    public PlayerUnregisterChannelEvent(PlayerConnection connection, String channel) {
        super(connection, channel);
    }
}
