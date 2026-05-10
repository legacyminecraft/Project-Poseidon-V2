package com.legacyminecraft.poseidon.network.handler;

import com.google.common.base.Preconditions;
import com.legacyminecraft.poseidon.network.connection.PlayerConnection;
import com.legacyminecraft.poseidon.network.packet.Packet;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
    public PacketHandlerPipeline<P> addFirst(String name, PacketHandler<P> handler) {
        return internalAdd(null, name, handler, AddStrategy.ADD_FIRST);
    }

    @Override
    public PacketHandlerPipeline<P> addLast(String name, PacketHandler<P> handler) {
        return internalAdd(null, name, handler, AddStrategy.ADD_LAST);
    }

    @Override
    public PacketHandlerPipeline<P> addBefore(String baseName, String name, PacketHandler<P> handler) {
        return internalAdd(baseName, name, handler, AddStrategy.ADD_BEFORE);
    }

    @Override
    public PacketHandlerPipeline<P> addAfter(String baseName, String name, PacketHandler<P> handler) {
        return internalAdd(baseName, name, handler, AddStrategy.ADD_AFTER);
    }

    private enum AddStrategy {
        ADD_FIRST,
        ADD_LAST,
        ADD_BEFORE,
        ADD_AFTER
    }

    private PacketHandlerPipeline<P> internalAdd(
            @Nullable String baseName,
            String name,
            PacketHandler<P> handler,
            AddStrategy strategy) {
        Preconditions.checkArgument(name != null, "name cannot be null");
        Preconditions.checkArgument(handler != null, "handler cannot be null");
        Preconditions.checkArgument(strategy != null, "strategy cannot be null");

        synchronized (this) {
            checkHandlerUnique(name);
            PacketHandlerContext<P> ctx = new PacketHandlerContext<>(name, handler);
            switch (strategy) {
                case ADD_FIRST -> this.handlers.addFirst(ctx);
                case ADD_LAST -> this.handlers.add(ctx);
                case ADD_BEFORE -> this.handlers.add(indexOfHandler(baseName), ctx);
                case ADD_AFTER -> this.handlers.add(indexOfHandler(baseName) + 1, ctx);
            }
            return this;
        }
    }

    private void checkHandlerUnique(String name) {
        for (PacketHandlerContext<P> ctx : this.handlers) {
            if (ctx.name().equals(name)) {
                throw new IllegalArgumentException("handler '" + name + "' already exists in this pipeline");
            }
        }
    }

    private int indexOfHandler(String baseName) {
        Preconditions.checkArgument(baseName != null, "baseName cannot be null");

        for (int i = 0; i < this.handlers.size(); i++) {
            PacketHandlerContext<P> ctx = this.handlers.get(i);
            if (ctx.name().equals(baseName)) {
                return i;
            }
        }
        throw new NoSuchElementException("handler '" + baseName + "' does not exist in this pipeline");
    }

    @Override
    public @Nullable PacketHandler<P> remove(String name) {
        Preconditions.checkArgument(name != null, "name cannot be null");

        synchronized (this) {
            for (int i = 0; i < this.handlers.size(); i++) {
                PacketHandlerContext<P> ctx = this.handlers.get(i);
                if (ctx.name().equals(name)) {
                    return this.handlers.remove(i).handler();
                }
            }
            return null;
        }
    }

    @Override
    public @Nullable PacketHandler<P> get(String name) {
        Preconditions.checkArgument(name != null, "name cannot be null");

        for (PacketHandlerContext<P> ctx : this.handlers) {
            if (ctx.name().equals(name)) {
                return ctx.handler();
            }
        }
        return null;
    }

    @Override
    public Map<String, PacketHandler<P>> toMap() {
        LinkedHashMap<String, PacketHandler<P>> map = new LinkedHashMap<>();
        for (PacketHandlerContext<P> ctx : this.handlers) {
            map.put(ctx.name(), ctx.handler());
        }
        return map;
    }

    public boolean invokeHandlers(P packet) {
        this.holder.setPacket(packet);
        Iterator<PacketHandlerContext<P>> iterator = this.handlers.iterator();
        boolean hasPacket;

        while ((hasPacket = this.holder.hasPacket()) && iterator.hasNext()) {
            PacketHandlerContext<P> ctx = iterator.next();
            try {
                ctx.handler().handlePacket(this.connection, this.holder);
            } catch (Throwable t) {
                log.warn("An error occurred while invoking handler {} with packet {}",
                        ctx.handler().getClass().getName(), packet.getClass().getName(), t);
            }
        }
        return hasPacket;
    }
}
