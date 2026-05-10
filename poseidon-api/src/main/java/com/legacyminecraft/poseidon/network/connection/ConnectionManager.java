package com.legacyminecraft.poseidon.network.connection;

import com.legacyminecraft.poseidon.network.packet.InboundPacket;
import com.legacyminecraft.poseidon.network.packet.MissingNoArgConstructorException;
import com.legacyminecraft.poseidon.network.packet.Packet;

import java.util.NoSuchElementException;

public interface ConnectionManager {

    /**
     * Registers a packet for this connection manager.
     * <p>
     * It is required that the packet id <u>and</u> the packet class are
     * unique.
     *
     * @param packetId the unique packet id
     * @param packetClass the unique packet class
     * @throws IllegalArgumentException if the packet id is less than {@code 0}
     *         or greater than {@code 255}
     * @throws IllegalStateException if a packet with this id or class is
     *         already registered
     * @throws MissingNoArgConstructorException if the packet is an
     *         {@link InboundPacket} and its class does not define an
     *         accessible no-argument constructor
     */
    void registerPacket(int packetId, Class<? extends Packet> packetClass);

    /**
     * Unregisters a packet for this connection manager by its id.
     *
     * @param packetId the packet id
     * @throws IllegalStateException if no packet with this id is registered
     */
    void unregisterPacket(int packetId);

    /**
     * Unregisters a packet for this connection manager by its class.
     *
     * @param packetClass the packet class
     * @throws IllegalStateException if no packet with this class is registered
     */
    void unregisterPacket(Class<? extends Packet> packetClass);

    /**
     * Gets a packet's id by its class, provided that it is registered.
     *
     * @param packetClass the packet class
     * @return the packet id
     * @throws NoSuchElementException if no packet with this class is registered
     */
    int getPacketId(Class<? extends Packet> packetClass);

    /**
     * Gets a packet's class by its id, provided that it is registered.
     *
     * @param packetId the packet id
     * @return the packet class
     * @throws NoSuchElementException if no packet with this id is registered
     */
    Class<? extends Packet> getPacketClass(int packetId);
}
