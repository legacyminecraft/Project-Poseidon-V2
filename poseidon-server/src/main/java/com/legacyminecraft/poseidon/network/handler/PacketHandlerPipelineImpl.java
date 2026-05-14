package com.legacyminecraft.poseidon.network.handler;

import com.google.common.base.Preconditions;
import com.legacyminecraft.poseidon.network.connection.PlayerConnection;
import com.legacyminecraft.poseidon.network.protocol.Packet;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class PacketHandlerPipelineImpl<P extends Packet> implements PacketHandlerPipeline<P> {

    private static final Logger log = LoggerFactory.getLogger(PacketHandlerPipelineImpl.class);

    private final PlayerConnection connection;
    private final List<PacketHandlerContext<P>> handlers = new CopyOnWriteArrayList<>();
    private final PacketHolderImpl<P> holder = new PacketHolderImpl<>(null);

    public PacketHandlerPipelineImpl(PlayerConnection connection) {
        this.connection = connection;
    }

    @Override
    public PlayerConnection getConnection() {
        return this.connection;
    }

    @Override
    public PacketHandlerPipeline<P> addHandler(byte priority, PacketHandler<P> handler) {
        Preconditions.checkArgument(handler != null, "handler cannot be null");

        synchronized (this) {
            PacketHandlerContext<P> ctx = new PacketHandlerContext<>(priority, handler);
            for (int i = 0; i < this.handlers.size(); i++) {
                PacketHandlerContext<P> other = this.handlers.get(i);
                if (ctx.priority() < other.priority()) {
                    this.handlers.add(i, ctx);
                    return this;
                }
            }
            this.handlers.add(ctx);
            return this;
        }
    }

    @Override
    public boolean removeHandler(PacketHandler<P> handler) {
        Preconditions.checkArgument(handler != null, "handler cannot be null");

        synchronized (this) {
            boolean removed = false;
            for (int i = 0; i < this.handlers.size(); i++) {
                PacketHandlerContext<P> ctx = this.handlers.get(i);
                if (ctx.handler().equals(handler)) {
                    this.handlers.remove(i--);
                    removed = true;
                }
            }
            return removed;
        }
    }

    public @Nullable P invokeHandlers(P packet) {
        this.holder.setPacket(packet);
        Iterator<PacketHandlerContext<P>> iterator = this.handlers.iterator();

        while (this.holder.hasPacket() && iterator.hasNext()) {
            PacketHandlerContext<P> ctx = iterator.next();
            try {
                ctx.handler().handlePacket(this.connection, this.holder);
            } catch (Throwable t) {
                log.warn("An error occurred while invoking handler {} with packet {}",
                        ctx.handler().getClass().getName(), packet.getClass().getName(), t);
            }
        }
        return this.holder.getPacket();
    }

    private record PacketHandlerContext<P extends Packet>(byte priority, PacketHandler<P> handler) {
    }
}
