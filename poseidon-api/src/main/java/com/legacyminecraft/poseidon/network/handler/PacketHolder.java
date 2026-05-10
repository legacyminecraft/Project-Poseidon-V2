package com.legacyminecraft.poseidon.network.handler;

import com.legacyminecraft.poseidon.network.packet.Packet;
import org.jspecify.annotations.Nullable;

/**
 * Represents a packet holder. The purpose of a packet holder is to allow
 * packets to be replaced or dropped before being handled by other
 * {@link PacketHandler}s.
 *
 * @param <P> the type of packet to be held by this holder
 */
public interface PacketHolder<P extends Packet> {

    /**
     * Returns if this holder contains a packet.
     *
     * @return {@code true} if this holder contains a packet
     */
    default boolean hasPacket() {
        return getPacket() != null;
    }

    /**
     * Returns the packet inside this holder.
     *
     * @return the packet, or null if it has been dropped
     */
    @Nullable P getPacket();

    /**
     * Sets the packet inside this holder.
     *
     * @param packet the packet
     */
    void setPacket(@Nullable P packet);

    /**
     * Drops the packet, preventing it from being handled further.
     */
    default void dropPacket() {
        setPacket(null);
    }
}
