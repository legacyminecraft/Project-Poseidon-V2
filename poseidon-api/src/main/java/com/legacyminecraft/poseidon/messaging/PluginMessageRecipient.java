package com.legacyminecraft.poseidon.messaging;

import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * Represents an object which is capable of receiving plugin messages.
 */
public interface PluginMessageRecipient {

    /**
     * Sends this recipient a plugin message on an outbound plugin channel.
     * <p>
     * The message may not be larger than {@link Messenger#MAX_MESSAGE_LENGTH}
     * bytes, and the channel must be registered to the server's messenger.
     * <p>
     * If this recipient is not listening on the channel, the plugin message
     * will not be sent.
     *
     * @param owningPlugin the plugin that owns the channel
     * @param channel the channel to send the plugin message on
     * @param message the plugin message data
     * @throws IllegalStateException if the channel is not registered or the
     *         specified plugin does not own the channel
     */
    void sendPluginMessage(Plugin owningPlugin, String channel, byte[] message);

    /**
     * Returns a set of all plugin channels that this recipient is listening
     * on.
     *
     * @return all channels that this recipient is listening on
     */
    Set<String> getListeningChannels();
}
