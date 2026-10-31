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
        NetLoginHandler netLoginHandler = loginProcessHandler.getNetLoginHandler();
        boolean onlineMode = loginProcessHandler.getServer().onlineMode;
        // Hybrid auth: offline mode servers still verify clients that announced a session in the handshake
        boolean hybrid = !onlineMode && netLoginHandler.isHybridAuthRequested();
        if (!onlineMode && !hybrid) {
            return;
        }

        boolean kickOnFailure = onlineMode || Poseidon.getConfig().sessions.hybridAuth.kickOnInvalidHybridSession;
        String name = loginProcessHandler.getPlayerName();
        try {
            String serverId = NetLoginHandler.a(netLoginHandler);
            InetAddress ipAddress = netLoginHandler.networkManager.getClientAddress().getAddress();
            if (Poseidon.getSessionService().verifySession(name, serverId, ipAddress)) {
                netLoginHandler.networkManager.setAuthenticated(true);
                if (hybrid) {
                    log.info("{} authenticated with the session server (hybrid auth)", name);
                }
            } else {
                log.info("{} tried to login with an invalid session", name);
                if (kickOnFailure) {
                    loginProcessHandler.disconnect("Invalid session");
                }
            }
        } catch (ServiceClientException e) {
            log.warn("Failed to verify session for {}", name, e);
            if (kickOnFailure) {
                loginProcessHandler.disconnect("Failed to verify session");
            }
        }
    }
}
