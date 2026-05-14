package com.legacyminecraft.poseidon.network.protocol;

import com.google.common.base.Preconditions;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketCodec;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketDecoder;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;
import org.jspecify.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ProtocolManagerImpl implements ProtocolManager {

    private final Map<Class<? extends OutboundPacket>, OutboundRegistration<?>> outboundRegistrations = new ConcurrentHashMap<>();
    private final Map<Integer, InboundRegistration<?>> inboundRegistrations = new ConcurrentHashMap<>();

    private record OutboundRegistration<P extends OutboundPacket>(int packetId, PacketEncoder<P> packetEncoder) {
    }

    private record InboundRegistration<P extends InboundPacket>(Class<P> packetClass, PacketDecoder<P> packetDecoder) {
    }

    @Override
    public <P extends OutboundPacket> boolean registerOutboundPacket(
            int packetId,
            Class<P> packetClass,
            PacketEncoder<P> packetEncoder) {
        Preconditions.checkArgument(packetClass != null, "packetClass cannot be null");
        Preconditions.checkArgument(packetEncoder != null, "packetEncoder cannot be null");
        Preconditions.checkArgument(packetId >= 0 && packetId <= 255, "packetId must be between 0 and 255");

        synchronized (this) {
            if (checkUniqueOutbound(packetId, packetClass)) {
                this.outboundRegistrations.put(packetClass, new OutboundRegistration<>(packetId, packetEncoder));
                return true;
            }
            return false;
        }
    }

    @Override
    public <P extends InboundPacket> boolean registerInboundPacket(
            int packetId,
            Class<P> packetClass,
            PacketDecoder<P> packetDecoder) {
        Preconditions.checkArgument(packetClass != null, "packetClass cannot be null");
        Preconditions.checkArgument(packetDecoder != null, "packetDecoder cannot be null");
        Preconditions.checkArgument(packetId >= 0 && packetId <= 255, "packetId must be between 0 and 255");

        synchronized (this) {
            if (checkUniqueInbound(packetId, packetClass)) {
                this.inboundRegistrations.put(packetId, new InboundRegistration<>(packetClass, packetDecoder));
                return true;
            }
            return false;
        }
    }

    @Override
    public <P extends DuplexPacket> boolean registerDuplexPacket(
            int packetId,
            Class<P> packetClass,
            PacketCodec<P> packetCodec) {
        Preconditions.checkArgument(packetClass != null, "packetClass cannot be null");
        Preconditions.checkArgument(packetCodec != null, "packetCodec cannot be null");
        Preconditions.checkArgument(packetId >= 0 && packetId <= 255, "packetId must be between 0 and 255");

        synchronized (this) {
            if (checkUniqueOutbound(packetId, packetClass) && checkUniqueInbound(packetId, packetClass)) {
                this.outboundRegistrations.put(packetClass, new OutboundRegistration<>(packetId, packetCodec));
                this.inboundRegistrations.put(packetId, new InboundRegistration<>(packetClass, packetCodec));
                return true;
            }
            return false;
        }
    }

    private boolean checkUniqueOutbound(int packetId, Class<? extends OutboundPacket> packetClass) {
        for (Map.Entry<Class<? extends OutboundPacket>, OutboundRegistration<?>> entry : this.outboundRegistrations.entrySet()) {
            if (entry.getKey().equals(packetClass) || entry.getValue().packetId() == packetId) {
                return false;
            }
        }
        return true;
    }

    private boolean checkUniqueInbound(int packetId, Class<? extends InboundPacket> packetClass) {
        for (Map.Entry<Integer, InboundRegistration<?>> entry : this.inboundRegistrations.entrySet()) {
            if (entry.getKey().equals(packetId) || entry.getValue().packetClass().equals(packetClass)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public <P extends OutboundPacket> boolean unregisterOutboundPacket(Class<P> packetClass) {
        Preconditions.checkArgument(packetClass != null, "packetClass cannot be null");

        synchronized (this) {
            return internalUnregisterOutbound(packetClass);
        }
    }

    @Override
    public <P extends InboundPacket> boolean unregisterInboundPacket(Class<P> packetClass) {
        Preconditions.checkArgument(packetClass != null, "packetClass cannot be null");

        synchronized (this) {
            return internalUnregisterInbound(packetClass);
        }
    }

    @Override
    public <P extends DuplexPacket> boolean unregisterDuplexPacket(Class<P> packetClass) {
        Preconditions.checkArgument(packetClass != null, "packetClass cannot be null");

        synchronized (this) {
            return internalUnregisterOutbound(packetClass) || internalUnregisterInbound(packetClass);
        }
    }

    private <P extends OutboundPacket> boolean internalUnregisterOutbound(Class<P> packetClass) {
        return this.outboundRegistrations.remove(packetClass) != null;
    }

    private <P extends InboundPacket> boolean internalUnregisterInbound(Class<P> packetClass) {
        Iterator<Map.Entry<Integer, InboundRegistration<?>>> iterator = this.inboundRegistrations.entrySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getValue().packetClass().equals(packetClass)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public @Nullable InboundPacket decodePacket(DataInput input) throws IOException {
        Preconditions.checkArgument(input != null, "input cannot be null");

        int packetId = input.readUnsignedByte();
        InboundRegistration<?> registration = this.inboundRegistrations.get(packetId);
        return registration == null ? null : registration.packetDecoder().decode(input);
    }

    public void encodePacket(OutboundPacket packet, DataOutput output) throws IOException {
        Preconditions.checkArgument(packet != null, "packet cannot be null");
        Preconditions.checkArgument(output != null, "output cannot be null");

        OutboundRegistration registration = this.outboundRegistrations.get(packet.getClass());
        if (registration == null) {
            throw new IllegalStateException("packet %s cannot be encoded because it has not been registered"
                    .formatted(packet.getClass().getName()));
        }
        output.writeByte(registration.packetId());
        registration.packetEncoder().encode(packet, output);
    }

    public void registerDefaults() {
    }
}
