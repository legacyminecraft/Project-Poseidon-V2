package com.legacyminecraft.poseidon.network.packet;

import java.io.DataOutput;
import java.io.IOException;

/**
 * Represents a packet which can be sent to a player.
 */
public non-sealed interface OutboundPacket extends Packet {

    /**
     * Writes the packet's data to the specified data output.
     *
     * @param output the data output
     * @throws IOException if an I/O error occurs
     */
    void writePacketData(DataOutput output) throws IOException;
}
