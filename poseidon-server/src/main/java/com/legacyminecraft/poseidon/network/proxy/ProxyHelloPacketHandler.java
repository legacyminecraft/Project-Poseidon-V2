package com.legacyminecraft.poseidon.network.proxy;

import com.google.gson.JsonParseException;
import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.network.connection.AbstractPlayerConnection;
import com.legacyminecraft.poseidon.network.connection.PlayerConnection;
import com.legacyminecraft.poseidon.network.handler.PacketHandler;
import com.legacyminecraft.poseidon.network.handler.PacketHolder;
import com.legacyminecraft.poseidon.network.protocol.InboundPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.nio.charset.StandardCharsets;

public final class ProxyHelloPacketHandler implements PacketHandler<InboundPacket> {

    public static final ProxyHelloPacketHandler INSTANCE = new ProxyHelloPacketHandler();

    private static final Logger log = LoggerFactory.getLogger(ProxyHelloPacketHandler.class);
    private static final String HELLO_TAG = "HELLO";

    private ProxyHelloPacketHandler() {
    }

    @Override
    public void handlePacket(PlayerConnection connection, PacketHolder<InboundPacket> holder) throws Exception {
        InboundPacket packet = holder.getPacket();
        if (!(packet instanceof ProxyMessage(String tag, byte[] data)) || !tag.equals(HELLO_TAG)) {
            return;
        }
        holder.dropPacket();

        DataInput input = new DataInputStream(new ByteArrayInputStream(data));
        ProxyHelloPacket hello = new ProxyHelloPacket(input);
        byte[] secret = Poseidon.getConfig().network.proxySupport.secret.getBytes(StandardCharsets.UTF_8);
        if (!hello.isSignatureValid(secret)) {
            log.warn("Failed to verify authenticity of proxy hello packet from {}", connection.getRawAddress());
            connection.disconnect("Failed to verify proxy connection details");
            return;
        }

        ProxyConnectionDetails details;
        try {
            details = hello.deserializeDetails();
        } catch (JsonParseException e) {
            log.warn("Received malformed proxy hello packet from {}", connection.getRawAddress(), e);
            connection.disconnect("Malformed proxy connection details");
            return;
        }

        if (connection instanceof AbstractPlayerConnection con) {
            con.onDetailsReceived(details);
        }
        connection.getInboundPipeline().removeHandler(this);
    }
}
