package com.legacyminecraft.poseidon.network.connection;

import com.legacyminecraft.poseidon.network.handler.PacketHandlerPipeline;
import com.legacyminecraft.poseidon.network.protocol.InboundPacket;
import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;

import java.net.InetSocketAddress;

/**
 * Represents a player's connection to the server.
 */
public interface PlayerConnection {

    /**
     * Sends a packet to the player.
     *
     * @param packet the packet to send
     * @return a future which will complete when the packet has been sent
     */
    ConnectionFuture sendPacket(OutboundPacket packet);

    /**
     * Disconnects the player.
     *
     * @param message the disconnect message
     */
    void disconnect(String message);

    /**
     * Returns a future which will complete when the player has disconnected.
     *
     * @return a future which will complete when the player has disconnected
     */
    ConnectionFuture getDisconnectFuture();

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
     * Returns the client address of the player. If the connection is behind
     * a proxy, this will be the actual player address extracted from the proxy
     * handshake.
     *
     * @return the client address
     */
    InetSocketAddress getClientAddress();

    /**
     * Returns the handler pipeline responsible for handling inbound packets.
     *
     * @return the inbound pipeline
     */
    PacketHandlerPipeline<InboundPacket> getInboundPipeline();

    /**
     * Returns the handler pipeline responsible for handling outbound packets.
     *
     * @return the outbound pipeline
     */
    PacketHandlerPipeline<OutboundPacket> getOutboundPipeline();
}
