package com.legacyminecraft.poseidon.network.handler;

import com.legacyminecraft.poseidon.network.connection.PlayerConnection;
import com.legacyminecraft.poseidon.network.protocol.Packet;

/**
 * Represents a packet handler.
 *
 * @param <P> the type of packet to be handled by this handler
 */
@FunctionalInterface
public interface PacketHandler<P extends Packet> {

    /**
     * Handles a packet.
     *
     * @param connection the associated connection
     * @param holder the packet holder
     */
    void handlePacket(PlayerConnection connection, PacketHolder<P> holder);
}
