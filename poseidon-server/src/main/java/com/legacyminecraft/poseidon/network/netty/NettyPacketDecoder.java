package com.legacyminecraft.poseidon.network.netty;

import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.event.network.ServerReceivePacketEvent;
import com.legacyminecraft.poseidon.network.login.LoginState;
import com.legacyminecraft.poseidon.network.ping.ServerListPingHandler;
import com.legacyminecraft.poseidon.network.protocol.InboundPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.timeout.ReadTimeoutException;
import net.minecraft.server.NetHandler;
import net.minecraft.server.Packet;

import java.io.IOException;
import java.util.List;

public final class NettyPacketDecoder extends ReplayingDecoder<Void> {

    private final NettyPlayerConnection connection;

    public NettyPacketDecoder(NettyPlayerConnection connection) {
        this.connection = connection;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        try (ByteBufInputStream input = new ByteBufInputStream(buf)) {
            if (Poseidon.getConfig().network.pingProtocol.enabled
                    && this.connection.getLoginState() == LoginState.INITIAL
                    && tryHandlePing(ctx, input)) {
                return;
            }

            InboundPacket packet = Poseidon.getProtocolManager().decodePacket(input);
            if (packet != null) {
                boolean handle = new ServerReceivePacketEvent(this.connection, packet).callEvent();
                if (handle && packet instanceof Packet nmsPacket) {
                    NetHandler netHandler = this.connection.getNetHandler();
                    netHandler.getServer().queueSyncTask(() -> nmsPacket.a(netHandler));
                }
            } else {
                this.connection.a("disconnect.endOfStream");
            }
        }
    }

    private boolean tryHandlePing(ChannelHandlerContext ctx, ByteBufInputStream input) throws IOException {
        ServerListPingHandler pingHandler = this.connection.getPingHandler();
        if (pingHandler == null) {
            input.mark(1);
            int packetId = input.readUnsignedByte();
            input.reset();
            if (packetId > 2 && packetId != 250) {
                pingHandler = this.connection.enablePingProtocol();
            }
        }

        if (pingHandler != null) {
            try (ByteBufOutputStream output = new ByteBufOutputStream(ctx.alloc().ioBuffer())) {
                pingHandler.handlePing(input, output);
                ctx.writeAndFlush(output.buffer());
                if (pingHandler.isClosed()) {
                    ctx.channel().close();
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.connection.a("disconnect.endOfStream");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof ReadTimeoutException) {
            this.connection.a("disconnect.timeout");
        } else {
            this.connection.handleException(cause);
        }
    }
}
