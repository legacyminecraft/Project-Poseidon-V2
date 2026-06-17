package com.legacyminecraft.poseidon.messaging;

import com.legacyminecraft.poseidon.network.connection.PlayerConnection;

/**
 * Represents a listener for a specific plugin channel, which is notified when
 * a client sends a plugin message on that channel.
 */
@FunctionalInterface
public interface PluginMessageListener {

    /**
     * This method is called when a plugin message is received from a client.
     *
     * @param source the {@link PlayerConnection} that sent the plugin message
     * @param channel the channel which the plugin message was sent on
     * @param message the plugin message data
     */
    void onPluginMessageReceived(PlayerConnection source, String channel, byte[] message);
}
