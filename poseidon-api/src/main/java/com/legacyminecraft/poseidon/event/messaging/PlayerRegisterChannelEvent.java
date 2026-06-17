package com.legacyminecraft.poseidon.event.messaging;

import com.legacyminecraft.poseidon.network.connection.PlayerConnection;

/**
 * This event is fired when a {@link PlayerConnection} registers for a plugin
 * channel.
 */
public class PlayerRegisterChannelEvent extends PlayerChannelEvent {

    public PlayerRegisterChannelEvent(PlayerConnection connection, String channel) {
        super(connection, channel);
    }
}
