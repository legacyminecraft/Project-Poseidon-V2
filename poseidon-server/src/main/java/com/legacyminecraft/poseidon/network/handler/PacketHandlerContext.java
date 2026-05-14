package com.legacyminecraft.poseidon.network.handler;

import com.google.common.base.Preconditions;
import com.legacyminecraft.poseidon.network.protocol.Packet;

public record PacketHandlerContext<P extends Packet>(String name, PacketHandler<P> handler) {

    public PacketHandlerContext {
        Preconditions.checkArgument(name != null, "name cannot be null");
        Preconditions.checkArgument(handler != null, "handler cannot be null");
    }
}
