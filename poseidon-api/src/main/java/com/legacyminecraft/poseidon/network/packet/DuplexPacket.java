package com.legacyminecraft.poseidon.network.packet;

/**
 * Represents a packet which can be both sent to and received from a player.
 */
public interface DuplexPacket extends InboundPacket, OutboundPacket {
}
