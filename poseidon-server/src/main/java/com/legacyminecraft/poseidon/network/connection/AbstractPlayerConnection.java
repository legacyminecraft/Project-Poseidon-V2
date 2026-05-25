package com.legacyminecraft.poseidon.network.connection;

import com.google.common.base.Preconditions;
import com.legacyminecraft.poseidon.network.handler.PacketHandlerPipeline;
import com.legacyminecraft.poseidon.network.handler.PacketHandlerPipelineImpl;
import com.legacyminecraft.poseidon.network.protocol.InboundPacket;
import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.proxy.ProxyConnectionDetails;
import com.legacyminecraft.poseidon.network.proxy.ProxyMessage;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractPlayerConnection implements PlayerConnection {

    protected final ConnectionFutureImpl disconnectFuture = new ConnectionFutureImpl(this);

    private final PacketHandlerPipelineImpl<InboundPacket> inboundPipeline = new PacketHandlerPipelineImpl<>(this);
    private final PacketHandlerPipelineImpl<OutboundPacket> outboundPipeline = new PacketHandlerPipelineImpl<>(this);

    private final AtomicBoolean proxyConnection = new AtomicBoolean(false);

    @Override
    public abstract ConnectionFuture sendPacket(OutboundPacket packet);

    @Override
    public ConnectionFuture sendProxyMessage(String tag, byte[] data) {
        Preconditions.checkArgument(tag != null, "tag cannot be null");
        Preconditions.checkArgument(data != null, "data cannot be null");

        if (!isProxyConnection()) {
            String name = Optional.ofNullable(getPlayer()).map(Player::getName).orElse(null);
            throw new IllegalStateException("player " + name + " is not connected through a proxy");
        }
        return sendPacket(new ProxyMessage(tag, data));
    }

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
    public boolean isProxyConnection() {
        return this.proxyConnection.get();
    }

    @Override
    public abstract InetSocketAddress getRawAddress();

    @Override
    public abstract InetSocketAddress getClientAddress();

    public abstract void setClientAddress(InetSocketAddress address);

    public void onDetailsReceived(ProxyConnectionDetails details) {
        if (!this.proxyConnection.compareAndSet(false, true)) {
            return;
        }
        setClientAddress(new InetSocketAddress(details.sourceHost(), details.sourcePort()));
    }

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
