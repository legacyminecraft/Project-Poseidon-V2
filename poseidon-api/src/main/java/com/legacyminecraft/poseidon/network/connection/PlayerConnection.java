package com.legacyminecraft.poseidon.network.connection;

import com.legacyminecraft.poseidon.messaging.PluginMessageRecipient;
import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.net.InetSocketAddress;

/**
 * Represents a player's connection to the server.
 */
public interface PlayerConnection extends PluginMessageRecipient {

    /**
     * Returns the player associated with this connection.
     *
     * @return the player, or null if the player has not finished logging in
     */
    @Nullable Player getPlayer();

    /**
     * Sends a packet to the player.
     *
     * @param packet the packet to send
     */
    void sendPacket(OutboundPacket packet);

    /**
     * Disconnects the player.
     *
     * @param message the disconnect message
     */
    void disconnect(String message);

    /**
     * Returns if the player is connected to the server.
     *
     * @return {@code true} if the player is connected
     */
    boolean isConnected();

    /**
     * Returns if this connection was established through a proxy.
     *
     * @return {@code true} if this connection is a proxy connection
     */
    boolean isProxyConnection();

    /**
     * Returns the raw address of this connection. This may be a proxy address
     * or the client address depending on how the connection was established.
     *
     * @return the raw address of this connection
     */
    InetSocketAddress getRawAddress();

    /**
     * Returns the client address of the player. If this is a proxy connection,
     * this will be the player's actual address extracted from the proxy hello
     * message.
     *
     * @return the client address
     */
    InetSocketAddress getClientAddress();

    /**
     * Returns this connection's estimated ping in milliseconds.
     * <p>
     * Note that this will return {@code 0} if the player has not finished
     * logging in.
     *
     * @return the ping
     */
    int getPing();
}
