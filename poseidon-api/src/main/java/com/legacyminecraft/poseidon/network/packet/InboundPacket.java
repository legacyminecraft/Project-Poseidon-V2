package com.legacyminecraft.poseidon.network.packet;

import java.io.DataInput;
import java.io.IOException;

/**
 * Represents a packet which can be received from a player.
 * <p>
 * <b>Note:</b> subclasses <u><b>must</b></u> define a no-argument constructor
 * for the server to be able to instantiate instances of the packet!
 */
public non-sealed interface InboundPacket extends Packet {

    /**
     * Reads the packet's data from the specified data input.
     *
     * @param input the data input
     * @throws IOException if an I/O error occurs
     */
    void readPacketData(DataInput input) throws IOException;
}
