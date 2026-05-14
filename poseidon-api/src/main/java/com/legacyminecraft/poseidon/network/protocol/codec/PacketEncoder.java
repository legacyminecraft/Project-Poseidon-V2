package com.legacyminecraft.poseidon.network.protocol.codec;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;

import java.io.DataOutput;
import java.io.IOException;

/**
 * Represents a function which encodes an outbound packet.
 *
 * @param <P> the type of packet to encode
 */
@FunctionalInterface
public interface PacketEncoder<P extends OutboundPacket> {

    /**
     * Encodes a packet to a data output.
     *
     * @param packet the packet
     * @param output the data output
     * @throws IOException if an I/O error occurs
     */
    void encode(P packet, DataOutput output) throws IOException;
}
