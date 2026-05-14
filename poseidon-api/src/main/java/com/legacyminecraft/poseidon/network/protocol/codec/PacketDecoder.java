package com.legacyminecraft.poseidon.network.protocol.codec;

import com.legacyminecraft.poseidon.network.protocol.InboundPacket;

import java.io.DataInput;
import java.io.IOException;

/**
 * Represents a function which decodes an inbound packet.
 *
 * @param <P> the type of packet to decode
 */
@FunctionalInterface
public interface PacketDecoder<P extends InboundPacket> {

    /**
     * Decodes a packet from a data input.
     *
     * @param input the data input
     * @return the decoded packet
     * @throws IOException if an I/O error occurs
     */
    P decode(DataInput input) throws IOException;
}
