package com.legacyminecraft.poseidon.network.handler;

import com.legacyminecraft.poseidon.network.connection.PlayerConnection;
import com.legacyminecraft.poseidon.network.protocol.Packet;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Represents a pipeline of {@link PacketHandler}s.
 *
 * @param <P> the type of packet to be handled by this pipeline
 */
public interface PacketHandlerPipeline<P extends Packet> {

    /**
     * Returns the connection associated with this pipeline.
     *
     * @return the connection
     */
    PlayerConnection getConnection();

    /**
     * Adds a handler at the first position of this pipeline.
     *
     * @param name the handler's unique name
     * @param handler the handler
     * @return this pipeline
     * @throws IllegalArgumentException if a handler with the same name already
     *         exists in this pipeline
     */
    PacketHandlerPipeline<P> addFirst(String name, PacketHandler<P> handler);

    /**
     * Adds a handler at the last position of this pipeline.
     *
     * @param name the handler's unique name
     * @param handler the handler
     * @return this pipeline
     * @throws IllegalArgumentException if a handler with the same name already
     *         exists in this pipeline
     */
    PacketHandlerPipeline<P> addLast(String name, PacketHandler<P> handler);

    /**
     * Adds a handler before a handler present in this pipeline.
     *
     * @param baseName the existing handler's name
     * @param name the handler's unique name
     * @param handler the handler
     * @return this pipeline
     * @throws NoSuchElementException if no handler with the specified
     *         {@code baseName} exists in this pipeline
     * @throws IllegalArgumentException if a handler with the same name already
     *         exists in this pipeline
     */
    PacketHandlerPipeline<P> addBefore(String baseName, String name, PacketHandler<P> handler);

    /**
     * Adds a handler after a handler present in this pipeline.
     *
     * @param baseName the existing handler's name
     * @param name the handler's unique name
     * @param handler the handler
     * @return this pipeline
     * @throws NoSuchElementException if no handler with the specified
     *         {@code baseName} exists in this pipeline
     * @throws IllegalArgumentException if a handler with the same name already
     *         exists in this pipeline
     */
    PacketHandlerPipeline<P> addAfter(String baseName, String name, PacketHandler<P> handler);

    /**
     * Removes a handler from this pipeline.
     *
     * @param name the handler's name
     * @return the removed handler, or null if no such handler existed
     */
    @Nullable PacketHandler<P> remove(String name);

    /**
     * Gets a handler present in this pipeline.
     *
     * @param name the handler's name
     * @return the handler, or null if no such handler exists
     */
    @Nullable PacketHandler<P> get(String name);

    /**
     * Returns a map of all handlers present in this pipeline.
     *
     * @return a map of all handlers
     */
    Map<String, PacketHandler<P>> toMap();
}
