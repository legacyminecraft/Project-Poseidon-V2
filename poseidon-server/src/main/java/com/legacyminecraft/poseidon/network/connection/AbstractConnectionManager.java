package com.legacyminecraft.poseidon.network.connection;

import com.google.common.base.Preconditions;
import com.legacyminecraft.poseidon.network.packet.InboundPacket;
import com.legacyminecraft.poseidon.network.packet.MissingNoArgConstructorException;
import com.legacyminecraft.poseidon.network.packet.OutboundPacket;
import com.legacyminecraft.poseidon.network.packet.Packet;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.NoSuchElementException;

public abstract class AbstractConnectionManager implements ConnectionManager {

    protected static final Int2ObjectOpenHashMap<Class<? extends Packet>> idToClassMap = new Int2ObjectOpenHashMap<>();
    protected static final Object2IntOpenHashMap<Class<? extends Packet>> classToIdMap = new Object2IntOpenHashMap<>();
    protected static final IntOpenHashSet outboundPackets = new IntOpenHashSet();
    protected static final IntOpenHashSet inboundPackets = new IntOpenHashSet();

    @Override
    public void registerPacket(int packetId, Class<? extends Packet> packetClass) {
        Preconditions.checkArgument(packetId >= 0, "packet id must be >= 0");
        Preconditions.checkArgument(packetId <= 255, "packet id must be <= 255");
        Preconditions.checkArgument(packetClass != null, "packet class cannot be null");
        Preconditions.checkState(!idToClassMap.containsKey(packetId), "packet is already registered");
        Preconditions.checkState(!classToIdMap.containsKey(packetClass), "packet is already registered");

        if (InboundPacket.class.isAssignableFrom(packetClass)) {
            try {
                packetClass.getDeclaredConstructor().setAccessible(true);
            } catch (Exception e) {
                throw new MissingNoArgConstructorException(packetClass, e);
            }
            inboundPackets.add(packetId);
        }
        if (OutboundPacket.class.isAssignableFrom(packetClass)) {
            outboundPackets.add(packetId);
        }
        idToClassMap.put(packetId, packetClass);
        classToIdMap.put(packetClass, packetId);
    }

    @Override
    public void unregisterPacket(int packetId) {
        Preconditions.checkState(idToClassMap.containsKey(packetId), "packet is not registered");

        Class<? extends com.legacyminecraft.poseidon.network.packet.Packet> packetClass = idToClassMap.remove(packetId);
        if (packetClass != null) {
            classToIdMap.removeInt(packetClass);
        }
        outboundPackets.remove(packetId);
        inboundPackets.remove(packetId);
    }

    @Override
    public void unregisterPacket(Class<? extends Packet> packetClass) {
        Preconditions.checkArgument(packetClass != null, "packet class cannot be null");
        Preconditions.checkState(classToIdMap.containsKey(packetClass), "packet is not registered");

        int packetId = classToIdMap.removeInt(packetClass);
        if (packetId != Integer.MIN_VALUE) {
            idToClassMap.remove(packetId);
            outboundPackets.remove(packetId);
            inboundPackets.remove(packetId);
        }
    }

    @Override
    public int getPacketId(Class<? extends Packet> packetClass) {
        Preconditions.checkArgument(packetClass != null, "packet class cannot be null");

        int packetId = classToIdMap.getInt(packetClass);
        if (packetId == Integer.MIN_VALUE) {
            throw new NoSuchElementException("packet is not registered");
        }
        return packetId;
    }

    @Override
    public Class<? extends Packet> getPacketClass(int packetId) {
        Class<? extends com.legacyminecraft.poseidon.network.packet.Packet> packetClass = idToClassMap.get(packetId);
        if (packetClass == null) {
            throw new NoSuchElementException("packet is not registered");
        }
        return packetClass;
    }

    static {
        idToClassMap.defaultReturnValue(null);
        classToIdMap.defaultReturnValue(Integer.MIN_VALUE);
    }
}
