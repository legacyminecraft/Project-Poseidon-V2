package com.legacyminecraft.poseidon.network.protocol;

import com.google.common.base.Preconditions;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketCodec;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketDecoder;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;
import net.minecraft.server.*;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ProtocolManagerImpl implements ProtocolManager {

    private static final Logger log = LoggerFactory.getLogger(ProtocolManagerImpl.class);

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

        try {
            int packetId = input.readUnsignedByte();
            InboundRegistration<?> registration = this.inboundRegistrations.get(packetId);
            if (registration == null) {
                log.info("Unknown inbound packet id: {}", packetId);
                return null;
            }
            return registration.packetDecoder().decode(input);
        } catch (EOFException _) {
            return null;
        }
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
        registerDuplexPacket(0, Packet0KeepAlive.class, Packet0KeepAlive.CODEC);
        registerDuplexPacket(1, Packet1Login.class, Packet1Login.CODEC);
        registerDuplexPacket(2, Packet2Handshake.class, Packet2Handshake.CODEC);
        registerDuplexPacket(3, Packet3Chat.class, Packet3Chat.CODEC);
        registerOutboundPacket(4, Packet4UpdateTime.class, Packet4UpdateTime.ENCODER);
        registerOutboundPacket(5, Packet5EntityEquipment.class, Packet5EntityEquipment.ENCODER);
        registerOutboundPacket(6, Packet6SpawnPosition.class, Packet6SpawnPosition.ENCODER);
        registerInboundPacket(7, Packet7UseEntity.class, Packet7UseEntity.DECODER);
        registerOutboundPacket(8, Packet8UpdateHealth.class, Packet8UpdateHealth.ENCODER);
        registerDuplexPacket(9, Packet9Respawn.class, Packet9Respawn.CODEC);
        registerDuplexPacket(10, Packet10Flying.class, Packet10Flying.CODEC);
        registerDuplexPacket(11, Packet11PlayerPosition.class, Packet11PlayerPosition.CODEC);
        registerDuplexPacket(12, Packet12PlayerLook.class, Packet12PlayerLook.CODEC);
        registerDuplexPacket(13, Packet13PlayerLookMove.class, Packet13PlayerLookMove.CODEC);
        registerInboundPacket(14, Packet14BlockDig.class, Packet14BlockDig.DECODER);
        registerInboundPacket(15, Packet15Place.class, Packet15Place.DECODER);
        registerInboundPacket(16, Packet16BlockItemSwitch.class, Packet16BlockItemSwitch.DECODER);
        registerOutboundPacket(17, Packet17.class, Packet17.ENCODER);
        registerDuplexPacket(18, Packet18ArmAnimation.class, Packet18ArmAnimation.CODEC);
        registerInboundPacket(19, Packet19EntityAction.class, Packet19EntityAction.DECODER);
        registerOutboundPacket(20, Packet20NamedEntitySpawn.class, Packet20NamedEntitySpawn.ENCODER);
        registerOutboundPacket(21, Packet21PickupSpawn.class, Packet21PickupSpawn.ENCODER);
        registerOutboundPacket(22, Packet22Collect.class, Packet22Collect.ENCODER);
        registerOutboundPacket(23, Packet23VehicleSpawn.class, Packet23VehicleSpawn.ENCODER);
        registerOutboundPacket(24, Packet24MobSpawn.class, Packet24MobSpawn.ENCODER);
        registerOutboundPacket(25, Packet25EntityPainting.class, Packet25EntityPainting.ENCODER);
        registerOutboundPacket(28, Packet28EntityVelocity.class, Packet28EntityVelocity.ENCODER);
        registerOutboundPacket(29, Packet29DestroyEntity.class, Packet29DestroyEntity.ENCODER);
        registerOutboundPacket(30, Packet30Entity.class, Packet30Entity.ENCODER);
        registerOutboundPacket(31, Packet31RelEntityMove.class, Packet31RelEntityMove.ENCODER);
        registerOutboundPacket(32, Packet32EntityLook.class, Packet32EntityLook.ENCODER);
        registerOutboundPacket(33, Packet33RelEntityMoveLook.class, Packet33RelEntityMoveLook.ENCODER);
        registerOutboundPacket(34, Packet34EntityTeleport.class, Packet34EntityTeleport.ENCODER);
        registerOutboundPacket(38, Packet38EntityStatus.class, Packet38EntityStatus.ENCODER);
        registerOutboundPacket(39, Packet39AttachEntity.class, Packet39AttachEntity.ENCODER);
        registerOutboundPacket(40, Packet40EntityMetadata.class, Packet40EntityMetadata.ENCODER);
        registerOutboundPacket(50, Packet50PreChunk.class, Packet50PreChunk.ENCODER);
        registerOutboundPacket(51, Packet51MapChunk.class, Packet51MapChunk.ENCODER);
        registerOutboundPacket(52, Packet52MultiBlockChange.class, Packet52MultiBlockChange.ENCODER);
        registerOutboundPacket(53, Packet53BlockChange.class, Packet53BlockChange.ENCODER);
        registerOutboundPacket(54, Packet54PlayNoteBlock.class, Packet54PlayNoteBlock.ENCODER);
        registerOutboundPacket(60, Packet60Explosion.class, Packet60Explosion.ENCODER);
        registerOutboundPacket(61, Packet61.class, Packet61.ENCODER);
        registerOutboundPacket(70, Packet70Bed.class, Packet70Bed.ENCODER);
        registerOutboundPacket(71, Packet71Weather.class, Packet71Weather.ENCODER);
        registerOutboundPacket(100, Packet100OpenWindow.class, Packet100OpenWindow.ENCODER);
        registerDuplexPacket(101, Packet101CloseWindow.class, Packet101CloseWindow.CODEC);
        registerInboundPacket(102, Packet102WindowClick.class, Packet102WindowClick.DECODER);
        registerOutboundPacket(103, Packet103SetSlot.class, Packet103SetSlot.ENCODER);
        registerOutboundPacket(104, Packet104WindowItems.class, Packet104WindowItems.ENCODER);
        registerOutboundPacket(105, Packet105CraftProgressBar.class, Packet105CraftProgressBar.ENCODER);
        registerDuplexPacket(106, Packet106Transaction.class, Packet106Transaction.CODEC);
        registerDuplexPacket(130, Packet130UpdateSign.class, Packet130UpdateSign.CODEC);
        registerOutboundPacket(131, Packet131.class, Packet131.ENCODER);
        registerOutboundPacket(200, Packet200Statistic.class, Packet200Statistic.ENCODER);
        registerDuplexPacket(250, Packet250PluginMessage.class, Packet250PluginMessage.CODEC);
        registerDuplexPacket(255, Packet255KickDisconnect.class, Packet255KickDisconnect.CODEC);
    }
}
