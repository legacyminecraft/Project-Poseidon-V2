package com.legacyminecraft.poseidon.network.handler;

import com.legacyminecraft.poseidon.network.protocol.Packet;
import org.jspecify.annotations.Nullable;

public final class PacketHolderImpl<P extends Packet> implements PacketHolder<P> {

    private volatile @Nullable P packet;

    public PacketHolderImpl(@Nullable P packet) {
        this.packet = packet;
    }

    @Override
    public @Nullable P getPacket() {
        return this.packet;
    }

    @Override
    public void setPacket(@Nullable P packet) {
        this.packet = packet;
    }
}
