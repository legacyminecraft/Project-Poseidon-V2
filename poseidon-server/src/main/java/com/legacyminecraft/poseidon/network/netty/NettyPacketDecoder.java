package com.legacyminecraft.poseidon.network.netty;

import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.event.network.ServerReceivePacketEvent;
import com.legacyminecraft.poseidon.network.protocol.InboundPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.timeout.ReadTimeoutException;
import net.minecraft.server.NetHandler;
import net.minecraft.server.Packet;

import java.util.List;

public final class NettyPacketDecoder extends ReplayingDecoder<Void> {

    private final NettyPlayerConnection connection;

    public NettyPacketDecoder(NettyPlayerConnection connection) {
        this.connection = connection;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        try (ByteBufInputStream input = new ByteBufInputStream(buf)) {
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
