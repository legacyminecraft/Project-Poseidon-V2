package com.legacyminecraft.poseidon.network.login;

import com.legacyminecraft.poseidon.event.player.AsyncPlayerPreLoginEvent;
import com.legacyminecraft.poseidon.profile.MinecraftProfile;
import com.legacyminecraft.poseidon.profile.PlayerProfileImpl;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class CallPreLoginEventsLoginStage implements LoginStage {

    private static final Logger log = LoggerFactory.getLogger(CallPreLoginEventsLoginStage.class);

    @Override
    public void run(LoginProcessHandler loginProcessHandler) {
        MinecraftProfile profile = loginProcessHandler.getProfile();
        InetAddress ipAddress = loginProcessHandler.getNetLoginHandler().networkManager.getClientAddress().getAddress();
        AsyncPlayerPreLoginEvent asyncEvent = new AsyncPlayerPreLoginEvent(new PlayerProfileImpl(profile), ipAddress);
        asyncEvent.callEvent();

        if (asyncEvent.getResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            loginProcessHandler.disconnect(asyncEvent.getKickMessage());
            return;
        }

        if (PlayerPreLoginEvent.getHandlerList().getRegisteredListeners().length != 0) {
            PlayerPreLoginEvent event = new PlayerPreLoginEvent(profile.name(), ipAddress);
            CompletableFuture<PlayerPreLoginEvent.Result> future = new CompletableFuture<>();
            loginProcessHandler.getServer().queueSyncTask(() -> {
                event.callEvent();
                future.complete(event.getResult());
            });

            try {
                if (future.get() != PlayerPreLoginEvent.Result.ALLOWED) {
                    loginProcessHandler.disconnect(event.getKickMessage());
                }
            } catch (InterruptedException | ExecutionException e) {
                log.warn("Failed to call sync {} for {}", event.getClass().getSimpleName(), profile.name(), e);
                loginProcessHandler.disconnect("Internal server error");
            }
        }
    }
}
