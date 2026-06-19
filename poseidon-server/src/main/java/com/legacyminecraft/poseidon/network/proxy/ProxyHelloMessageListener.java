package com.legacyminecraft.poseidon.network.proxy;

import com.google.common.io.ByteStreams;
import com.google.gson.JsonParseException;
import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.messaging.PluginMessageListener;
import com.legacyminecraft.poseidon.network.connection.AbstractPlayerConnection;
import com.legacyminecraft.poseidon.network.connection.PlayerConnection;
import com.legacyminecraft.poseidon.network.login.LoginState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public final class ProxyHelloMessageListener implements PluginMessageListener {

    public static final ProxyHelloMessageListener INSTANCE = new ProxyHelloMessageListener();

    private static final Logger log = LoggerFactory.getLogger(ProxyHelloMessageListener.class);

    @Override
    public void onPluginMessageReceived(PlayerConnection source, String channel, byte[] message) {
        if (!(source instanceof AbstractPlayerConnection connection)) {
            return;
        }

        if (connection.getLoginState() != LoginState.INITIAL) {
            connection.disconnect("Unexpected proxy hello message");
            return;
        }

        if (!Poseidon.getConfig().network.proxySupport.enabled) {
            connection.disconnect("This server does not support connecting through a proxy");
            return;
        }

        connection.setLoginState(LoginState.PROXY);
        ProxyHelloMessage helloMessage;
        try {
            helloMessage = new ProxyHelloMessage(ByteStreams.newDataInput(message));
            byte[] secret = Poseidon.getConfig().network.proxySupport.secret.getBytes(StandardCharsets.UTF_8);
            if (!helloMessage.isSignatureValid(secret)) {
                log.warn("Received proxy hello message with invalid signature from {}", connection.getRawAddress());
                connection.disconnect("Invalid signature");
                return;
            }
        } catch (Exception e) {
            log.error("Failed to verify authenticity of proxy hello message from {}", connection.getRawAddress(), e);
            connection.disconnect("Failed to verify proxy hello message");
            return;
        }

        try {
            ProxyConnectionDetails details = helloMessage.deserializeDetails();
            connection.onConnectionDetailsReceived(details);
        } catch (JsonParseException e) {
            log.warn("Received malformed proxy hello message from {}", connection.getRawAddress(), e);
            connection.disconnect("Malformed proxy hello message");
        }
    }
}
