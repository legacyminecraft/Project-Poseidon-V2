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
     * Returns the connection flags the client sent when logging in.
     * <p>
     * If the most significant bit is set to {@code 1}, the client is
     * expected to support plugin messaging.
     *
     * @return the connection flags
     */
    byte getConnectionFlags();

    /**
     * Returns the flag bitmask the client appended to its handshake name after
     * a {@code ;}. The mask is written in base 36 and never exceeds 12
     * characters in length. For example, {@code Name;hra0ht} means bits
     * {@code 0} and {@code 30} are set.
     * <p>
     * Bits 0 to 29 are features and bits 30 and up are client identities.
     * <p>
     * Known bits:
     * <ul>
     *     <li>{@code 0}: The client has a session and can authenticate
     *     <li>{@code 30}: The client is Beta Evolutions
     * </ul>
     *
     * @return the flag bitmask, or 0 if the client sent none
     */
    long getHandshakeFlags();

    /**
     * Returns if the client set the given bit in its handshake flags.
     *
     * @param bit the bit number, from 0 to 61
     * @return {@code true} if the client set the bit
     * @see #getHandshakeFlags()
     */
    boolean hasHandshakeFlag(int bit);

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
     * this will be the player's actual address forwarded by the proxy.
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
