package com.legacyminecraft.poseidon.network.handler;

import com.legacyminecraft.poseidon.network.connection.PlayerConnection;
import com.legacyminecraft.poseidon.network.protocol.Packet;

/**
 * Represents a pipeline of {@link PacketHandler}s.
 * <p>
 * A pipeline calls its handlers in the order of their priority, from lowest
 * to highest.
 *
 * @param <P> the type of packet to be accepted by this pipeline
 */
public interface PacketHandlerPipeline<P extends Packet> {

    byte LOWEST_PRIORITY = -128;
    byte LOW_PRIORITY = -64;
    byte NORMAL_PRIORITY = 0;
    byte HIGH_PRIORITY = 64;
    byte HIGHEST_PRIORITY = 127;

    /**
     * Returns the connection associated with this pipeline.
     *
     * @return the connection
     */
    PlayerConnection getConnection();

    /**
     * Adds a handler to this pipeline.
     * <p>
     * The {@code priority} determines if the handler will be called earlier
     * or later in the pipeline.
     *
     * @param priority the priority, between {@code -128} (lowest) and
     *                 {@code 127} (highest)
     * @param handler the handler to add
     * @return this pipeline
     */
    PacketHandlerPipeline<P> addHandler(byte priority, PacketHandler<P> handler);

    /**
     * Removes a handler from this pipeline.
     *
     * @param handler the handler to remove
     * @return {@code true} if the handler was removed, {@code false} if the
     *         handler was not present in the pipeline
     */
    boolean removeHandler(PacketHandler<P> handler);
}
