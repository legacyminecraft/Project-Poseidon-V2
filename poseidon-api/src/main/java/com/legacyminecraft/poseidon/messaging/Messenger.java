package com.legacyminecraft.poseidon.messaging;

import com.legacyminecraft.poseidon.network.connection.PlayerConnection;
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * A messenger is responsible for managing registrations of inbound and
 * outbound plugin channels. It also notifies clients if inbound plugin
 * channels are registered or unregistered.
 */
public interface Messenger {

    /**
     * The maximum allowed length of a plugin channel.
     */
    int MAX_CHANNEL_LENGTH = 64;

    /**
     * The maximum allowed length of a plugin message.
     */
    int MAX_MESSAGE_LENGTH = 32767;

    /**
     * Registers an inbound plugin channel to this messenger, allowing it to
     * receive plugin messages on that channel.
     * <p>
     * If the plugin channel was registered successfully, all connected clients
     * will be notified.
     * <p>
     * Only <b>one</b> plugin can register a specific inbound channel to the
     * messenger.
     *
     * @param owningPlugin the plugin that should own the channel
     * @param channel the channel to receive messages on
     * @param listener the listener that will receive plugin messages
     * @return {@code true} if the channel was registered successfully,
     *         {@code false} if another plugin already registered this channel
     */
    boolean registerInboundChannel(Plugin owningPlugin, String channel, PluginMessageListener listener);

    /**
     * Registers an outbound plugin channel to this messenger, allowing it to
     * send plugin messages on that channel.
     * <p>
     * Only <b>one</b> plugin can register a specific outbound channel to the
     * messenger.
     *
     * @param owningPlugin the plugin that should own the channel
     * @param channel the channel to send messages on
     * @return {@code true} if the channel was registered successfully,
     *         {@code false} if another plugin already registered this channel
     */
    boolean registerOutboundChannel(Plugin owningPlugin, String channel);

    /**
     * Unregisters an inbound plugin channel owned by a plugin from this
     * messenger, no longer allowing it to receive plugin messages on that
     * channel.
     * <p>
     * If the plugin channel was unregistered successfully, all connected
     * clients will be notified.
     *
     * @param owningPlugin the plugin that owns the channel
     * @param channel the channel to stop receiving messages on
     * @return {@code true} if the channel was unregistered successfully,
     *         {@code false} if the channel is not registered or is not owned
     *         by the specified plugin
     */
    boolean unregisterInboundChannel(Plugin owningPlugin, String channel);

    /**
     * Unregisters all inbound plugin channels owned by a plugin from this
     * messenger, no longer allowing it to receive any plugin messages.
     * <p>
     * If any plugin channels were unregistered, all connected clients will be
     * notified.
     *
     * @param owningPlugin the plugin that should no longer receive plugin
     *        messages
     */
    void unregisterInboundChannels(Plugin owningPlugin);

    /**
     * Unregisters an outbound plugin channel owned by a plugin from this
     * messenger, no longer allowing it to send plugin messages on that
     * channel.
     *
     * @param owningPlugin the plugin that owns the channel
     * @param channel the channel to stop sending messages on
     * @return {@code true} if the channel was unregistered successfully,
     *         {@code false} if the channel is not registered or is not owned
     *         by the specified plugin
     */
    boolean unregisterOutboundChannel(Plugin owningPlugin, String channel);

    /**
     * Unregisters all outbound plugin channels owned by a plugin from this
     * messenger, no longer allowing it to send any plugin messages.
     *
     * @param owningPlugin the plugin that should no longer send plugin
     *        messages
     */
    void unregisterOutboundChannels(Plugin owningPlugin);

    /**
     * Returns if an inbound plugin channel is registered to this
     * messenger, and if it is owned by a specific plugin.
     *
     * @param owningPlugin the plugin that is assumed to own the channel
     * @param channel the channel to check for
     * @return {@code true} if the inbound channel is registered and is owned
     *         by the plugin, {@code false} otherwise
     */
    boolean isInboundChannelRegistered(Plugin owningPlugin, String channel);

    /**
     * Returns if an outbound plugin channel is registered to this
     * messenger, and if it is owned by a specific plugin.
     *
     * @param owningPlugin the plugin that is assumed to own the channel
     * @param channel the channel to check for
     * @return {@code true} if the outbound channel is registered and is owned
     *         by the plugin, {@code false} otherwise
     */
    boolean isOutboundChannelRegistered(Plugin owningPlugin, String channel);

    /**
     * Returns a set of all registered inbound plugin channels.
     *
     * @return all registered inbound channels
     */
    Set<String> getInboundChannels();

    /**
     * Returns a set of all registered inbound plugin channels owned by a
     * plugin.
     *
     * @param owningPlugin the plugin that owns the channels
     * @return all registered inbound channels owned by the plugin
     */
    Set<String> getInboundChannels(Plugin owningPlugin);

    /**
     * Returns a set of all registered outbound plugin channels.
     *
     * @return all registered outbound channels
     */
    Set<String> getOutboundChannels();

    /**
     * Returns a set of all registered outbound plugin channels owned by a
     * plugin.
     *
     * @param owningPlugin the plugin that owns the channels
     * @return all registered outbound channels owned by the plugin
     */
    Set<String> getOutboundChannels(Plugin owningPlugin);

    /**
     * Dispatches an inbound plugin message to the registered listener for the
     * channel it was sent on.
     *
     * @param source the {@link PlayerConnection} that sent the plugin message
     * @param channel the channel which the plugin message was sent on
     * @param message the plugin message data
     */
    void dispatchInboundMessage(PlayerConnection source, String channel, byte[] message);
}
