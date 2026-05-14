package com.legacyminecraft.poseidon.network.protocol;

import com.legacyminecraft.poseidon.network.protocol.codec.PacketCodec;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketDecoder;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;

/**
 * Represents a protocol manager which can be used to register and unregister
 * packets in order to expand the network protocol.
 */
public interface ProtocolManager {

    /**
     * Registers an outbound packet.
     * <p>
     * <b>Note:</b> It is required that the packet id and packet class are
     * unique among all registered outbound packets.
     *
     * @param packetId the packet id
     * @param packetClass the packet class
     * @param packetEncoder the packet encoder
     * @param <P> the type of packet to register
     * @return {@code true} if the packet was registered, {@code false} if
     *         a packet with this id or class has already been registered
     * @throws IllegalArgumentException if the packet id is not between
     *         {@code 0} and {@code 255}
     */
    <P extends OutboundPacket> boolean registerOutboundPacket(
            int packetId,
            Class<P> packetClass,
            PacketEncoder<P> packetEncoder);

    /**
     * Registers an inbound packet.
     * <p>
     * <b>Note:</b> It is required that the packet id and packet class are
     * unique among all registered inbound packets.
     *
     * @param packetId the packet id
     * @param packetClass the packet class
     * @param packetDecoder the packet decoder
     * @param <P> the type of packet to register
     * @return {@code true} if the packet was registered, {@code false} if
     *         a packet with this id or class has already been registered
     * @throws IllegalArgumentException if the packet id is not between
     *         {@code 0} and {@code 255}
     */
    <P extends InboundPacket> boolean registerInboundPacket(
            int packetId,
            Class<P> packetClass,
            PacketDecoder<P> packetDecoder);

    /**
     * Registers a duplex packet.
     * <p>
     * <b>Note:</b> It is required that the packet id and packet class are
     * unique among all registered outbound and inbound packets.
     *
     * @param packetId the packet id
     * @param packetClass the packet class
     * @param packetCodec the packet codec
     * @param <P> the type of packet to register
     * @return {@code true} if the packet was registered, {@code false} if
     *         a packet with this id or class has already been registered
     * @throws IllegalArgumentException if the packet id is not between
     *         {@code 0} and {@code 255}
     */
    <P extends DuplexPacket> boolean registerDuplexPacket(
            int packetId,
            Class<P> packetClass,
            PacketCodec<P> packetCodec);

    /**
     * Unregisters an outbound packet.
     *
     * @param packetClass the packet class
     * @param <P> the type of packet to unregister
     * @return {@code true} if the packet was unregistered, {@code false} if no
     *         packet with this class was registered
     */
    <P extends OutboundPacket> boolean unregisterOutboundPacket(Class<P> packetClass);

    /**
     * Unregisters an inbound packet.
     *
     * @param packetClass the packet class
     * @param <P> the type of packet to unregister
     * @return {@code true} if the packet was unregistered, {@code false} if no
     *         packet with this class was registered
     */
    <P extends InboundPacket> boolean unregisterInboundPacket(Class<P> packetClass);

    /**
     * Unregisters a duplex packet.
     *
     * @param packetClass the packet class
     * @param <P> the type of packet to unregister
     * @return {@code true} if the packet was unregistered, {@code false} if no
     *         packet with this class was registered
     */
    <P extends DuplexPacket> boolean unregisterDuplexPacket(Class<P> packetClass);
}
