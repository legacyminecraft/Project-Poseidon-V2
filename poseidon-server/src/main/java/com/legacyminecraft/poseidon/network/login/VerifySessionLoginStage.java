package com.legacyminecraft.poseidon.network.login;

import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.service.ServiceClientException;
import net.minecraft.server.NetLoginHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

public final class VerifySessionLoginStage implements LoginStage {

    private static final Logger log = LoggerFactory.getLogger(VerifySessionLoginStage.class);

    @Override
    public void run(LoginProcessHandler loginProcessHandler) {
        if (!loginProcessHandler.getServer().onlineMode) {
            return;
        }

        String name = loginProcessHandler.getPlayerName();
        NetLoginHandler netLoginHandler = loginProcessHandler.getNetLoginHandler();
        try {
            String serverId = NetLoginHandler.a(netLoginHandler);
            InetAddress ipAddress = netLoginHandler.getSocket().getInetAddress();
            if (!Poseidon.getSessionService().verifySession(name, serverId, ipAddress)) {
                log.info("{} tried to login with an invalid session", name);
                loginProcessHandler.disconnect("Invalid session");
            }
        } catch (ServiceClientException e) {
            log.warn("Failed to verify session for {}", name, e);
            loginProcessHandler.disconnect("Failed to verify session");
        }
    }
}
