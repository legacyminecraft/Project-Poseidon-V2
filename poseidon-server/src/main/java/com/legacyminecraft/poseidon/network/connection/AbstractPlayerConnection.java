package com.legacyminecraft.poseidon.network.connection;

import com.legacyminecraft.poseidon.network.handler.PacketHandlerPipeline;
import com.legacyminecraft.poseidon.network.handler.PacketHandlerPipelineImpl;
import com.legacyminecraft.poseidon.network.protocol.InboundPacket;
import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import org.jspecify.annotations.Nullable;

import java.net.InetSocketAddress;

public abstract class AbstractPlayerConnection implements PlayerConnection {

    protected final ConnectionFutureImpl disconnectFuture = new ConnectionFutureImpl(this);
    protected final PacketHandlerPipelineImpl<InboundPacket> inboundPipeline = new PacketHandlerPipelineImpl<>(this);
    protected final PacketHandlerPipelineImpl<OutboundPacket> outboundPipeline = new PacketHandlerPipelineImpl<>(this);

    @Override
    public abstract ConnectionFuture sendPacket(OutboundPacket packet);

    @Override
    public abstract void disconnect(String message);

    @Override
    public ConnectionFuture getDisconnectFuture() {
        return this.disconnectFuture;
    }

    @Override
    public boolean isConnected() {
        return !this.disconnectFuture.isCompleted();
    }

    @Override
    public abstract boolean isProxyConnection();

    @Override
    public abstract InetSocketAddress getRawAddress();

    @Override
    public abstract InetSocketAddress getClientAddress();

    @Override
    public PacketHandlerPipeline<InboundPacket> getInboundPipeline() {
        return this.inboundPipeline;
    }

    @Override
    public PacketHandlerPipeline<OutboundPacket> getOutboundPipeline() {
        return this.outboundPipeline;
    }

    public @Nullable InboundPacket invokeInboundHandlers(InboundPacket packet) {
        return this.inboundPipeline.invokeHandlers(packet);
    }

    public @Nullable OutboundPacket invokeOutboundHandlers(OutboundPacket packet) {
        return this.outboundPipeline.invokeHandlers(packet);
    }
}
