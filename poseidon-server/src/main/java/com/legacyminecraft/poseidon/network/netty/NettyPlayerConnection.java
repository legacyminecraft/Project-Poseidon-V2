package com.legacyminecraft.poseidon.network.netty;

import com.legacyminecraft.poseidon.network.connection.AbstractPlayerConnection;
import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.server.NetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public final class NettyPlayerConnection extends AbstractPlayerConnection {

    private static final Logger log = LoggerFactory.getLogger(NettyPlayerConnection.class);

    private final SocketChannel channel;
    private final InetSocketAddress rawAddress;
    private InetSocketAddress clientAddress;
    private NetHandler netHandler;

    public NettyPlayerConnection(SocketChannel channel, NetHandler netHandler) {
        this.channel = channel;
        this.rawAddress = channel.remoteAddress();
        this.clientAddress = channel.remoteAddress();
        this.netHandler = netHandler;

        this.channel.pipeline()
                .addLast("timeout-handler", new ReadTimeoutHandler(30000, TimeUnit.MILLISECONDS))
                .addLast("packet-decoder", new NettyPacketDecoder(this))
                .addLast("packet-encoder", new NettyPacketEncoder(this));
    }

    public NetHandler getNetHandler() {
        return this.netHandler;
    }

    @Override
    public void sendPacket(OutboundPacket packet) {
        queue(packet);
    }

    @Override
    public InetSocketAddress getRawAddress() {
        return this.rawAddress;
    }

    @Override
    public InetSocketAddress getClientAddress() {
        return getSocketAddress();
    }

    @Override
    public void setClientAddress(InetSocketAddress address) {
        this.clientAddress = address;
    }

    @Override
    public void a(NetHandler netHandler) {
        this.netHandler = netHandler;
    }

    @Override
    public void queue(OutboundPacket packet) {
        if (this.channel.eventLoop().inEventLoop()) {
            this.channel.write(packet);
        } else {
            this.channel.eventLoop().execute(() -> this.channel.write(packet));
        }
    }

    public void flush() {
        this.channel.flush();
    }

    @Override
    public void a() {
    }

    @Override
    public void a(String s, Object... aobject) {
        getNetHandler().a(s, aobject);
        this.channel.close();
    }

    public void handleException(Throwable t) {
        log.error("Connection error occurred", t);
        a("disconnect.genericReason", "Internal exception: " + t);
    }

    @Override
    public void b() {
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return this.clientAddress;
    }

    @Override
    public void d() {
        this.channel.close();
    }

    @Override
    public int e() {
        return 0;
    }
}
