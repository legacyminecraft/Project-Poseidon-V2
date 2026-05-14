package com.legacyminecraft.poseidon.network.protocol;

/**
 * Represents a packet which can be both sent to and received from a player.
 */
public interface DuplexPacket extends OutboundPacket, InboundPacket {
}
