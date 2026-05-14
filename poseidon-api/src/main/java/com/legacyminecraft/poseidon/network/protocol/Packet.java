package com.legacyminecraft.poseidon.network.protocol;

/**
 * Represents a network packet.
 */
public sealed interface Packet permits OutboundPacket, InboundPacket {
}
