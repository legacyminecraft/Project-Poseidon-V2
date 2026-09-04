package com.legacyminecraft.poseidon.network.proxy;

import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.messaging.PluginMessageListener;
import com.legacyminecraft.poseidon.network.connection.AbstractPlayerConnection;
import com.legacyminecraft.poseidon.network.connection.PlayerConnection;
import com.legacyminecraft.poseidon.network.login.LoginState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public final class ForwardedPlayerDataListener implements PluginMessageListener {

    public static final ForwardedPlayerDataListener INSTANCE = new ForwardedPlayerDataListener();

    private static final Logger log = LoggerFactory.getLogger(ForwardedPlayerDataListener.class);

    @Override
    public void onPluginMessageReceived(PlayerConnection source, String channel, byte[] message) {
        if (!(source instanceof AbstractPlayerConnection connection)) {
            return;
        }

        if (connection.getLoginState() != LoginState.INITIAL) {
            connection.disconnect("Unexpected forwarded player data payload");
            return;
        }

        if (!Poseidon.getConfig().network.proxySupport.enabled) {
            connection.disconnect("This server does not support connecting through a proxy");
            return;
        }

        connection.setLoginState(LoginState.PROXY);
        try {
            byte[] secret = Poseidon.getConfig().network.proxySupport.secret.getBytes(StandardCharsets.UTF_8);
            byte[] forwardedData = PlayerDataForwarding.verifySignature(message, secret);
            ForwardedPlayerData playerData = PlayerDataForwarding.readForwardedData(forwardedData);
            connection.receivedPlayerData(playerData);
        } catch (InvalidSignatureException e) {
            log.warn("Received forwarded player data with invalid signature from {}", connection.getRawAddress());
            connection.disconnect("Invalid signature");
        }
    }
}
