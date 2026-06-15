package com.legacyminecraft.poseidon.network.netty;

import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.event.network.ServerSendPacketEvent;
import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public final class NettyPacketEncoder extends MessageToByteEncoder<OutboundPacket> {

    private final NettyPlayerConnection connection;

    public NettyPacketEncoder(NettyPlayerConnection connection) {
        this.connection = connection;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, OutboundPacket packet, ByteBuf buf) {
        try (ByteBufOutputStream output = new ByteBufOutputStream(buf)) {
            boolean send = new ServerSendPacketEvent(this.connection, packet).callEvent();
            if (send) {
                Poseidon.getProtocolManager().encodePacket(packet, output);
            }
        } catch (Exception e) {
            this.connection.handleException(e);
        }
    }
}
