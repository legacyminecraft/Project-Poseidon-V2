package com.legacyminecraft.poseidon.network.packet;

/**
 * Represents a network packet.
 */
public sealed interface Packet permits InboundPacket, OutboundPacket {
}
