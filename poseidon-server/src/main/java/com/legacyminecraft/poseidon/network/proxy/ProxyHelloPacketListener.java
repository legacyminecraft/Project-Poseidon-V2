package com.legacyminecraft.poseidon.network.proxy;

import com.google.gson.JsonParseException;
import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.event.network.ServerReceivePacketEvent;
import com.legacyminecraft.poseidon.network.connection.AbstractPlayerConnection;
import com.legacyminecraft.poseidon.network.login.LoginState;
import com.legacyminecraft.poseidon.network.protocol.InboundPacket;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.nio.charset.StandardCharsets;

public final class ProxyHelloPacketListener implements Listener {

    public static final ProxyHelloPacketListener INSTANCE = new ProxyHelloPacketListener();

    private static final Logger log = LoggerFactory.getLogger(ProxyHelloPacketListener.class);
    private static final String HELLO_TAG = "HELLO";

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onServerReceivePacket(ServerReceivePacketEvent event) throws Exception {
        if (!(event.getConnection() instanceof AbstractPlayerConnection connection)) {
            return;
        }

        InboundPacket packet = event.getPacket();
        if (!(packet instanceof ProxyMessage(String tag, byte[] data)) || !tag.equals(HELLO_TAG)) {
            return;
        }
        event.setCancelled(true);

        if (connection.getLoginState() != LoginState.INITIAL) {
            connection.disconnect("Unexpected proxy hello packet");
            return;
        }
        if (!Poseidon.getConfig().network.proxySupport.enabled) {
            connection.disconnect("This server does not support connecting through a proxy");
            return;
        }
        connection.setLoginState(LoginState.PROXY);

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

        connection.onDetailsReceived(details);
    }
}
