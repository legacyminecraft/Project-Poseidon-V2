package com.legacyminecraft.poseidon.network;

import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.profile.MinecraftProfile;
import com.legacyminecraft.poseidon.profile.PlayerProfileImpl;
import com.legacyminecraft.poseidon.profile.ProfileLookupCallback;
import com.legacyminecraft.poseidon.profile.ProfileNotFoundException;
import com.legacyminecraft.poseidon.profile.UuidUtil;
import com.legacyminecraft.poseidon.service.ServiceClientException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetLoginHandler;
import net.minecraft.server.Packet1Login;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class LoginProcessHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(LoginProcessHandler.class);

    private final MinecraftServer server;
    private final NetLoginHandler netLoginHandler;
    private final Packet1Login loginPacket;

    public LoginProcessHandler(MinecraftServer server, NetLoginHandler netLoginHandler, Packet1Login loginPacket) {
        this.server = server;
        this.netLoginHandler = netLoginHandler;
        this.loginPacket = loginPacket;
    }

    @Override
    public void run() {
        if (!this.server.onlineMode) {
            getPlayerProfile();
        } else {
            verifySession();
        }
    }

    private void verifySession() {
        try {
            String name = getPlayerName();
            String serverId = NetLoginHandler.a(this.netLoginHandler);
            InetAddress ipAddress = this.netLoginHandler.getSocket().getInetAddress();
            if (Poseidon.getSessionService().verifySession(name, serverId, ipAddress)) {
                getPlayerProfile();
            } else {
                log.info("{} tried to login with an invalid session", getPlayerName());
                disconnect("Invalid session");
            }
        } catch (ServiceClientException e) {
            log.warn("Failed to verify session for {}", getPlayerName(), e);
            disconnect("Failed to verify session");
        }
    }

    private void getPlayerProfile() {
        Optional<MinecraftProfile> optional = Poseidon.getProfileCache().getProfile(getPlayerName(), true);
        if (optional.isPresent() && optional.get().onlineMode()) {
            MinecraftProfile profile = new MinecraftProfile(optional.get().id(), getPlayerName(), optional.get().onlineMode());
            callPreLoginEvents(profile);
        } else {
            Poseidon.getProfileService().lookupProfileByName(getPlayerName(), new ProfileLookupCallback() {
                @Override
                public void onLookupSuccess(MinecraftProfile profile) {
                    MinecraftProfile corrected = new MinecraftProfile(profile.id(), getPlayerName(), profile.onlineMode());
                    Poseidon.getProfileCache().addProfile(corrected);
                    callPreLoginEvents(corrected);
                }

                @Override
                public void onLookupFailure(Throwable cause) {
                    if (cause instanceof ProfileNotFoundException) {
                        // TODO: make handling of unknown profiles configurable
                        String name = getPlayerName();
                        MinecraftProfile offlineProfile = new MinecraftProfile(UuidUtil.createOfflineUuid(name), name, false);
                        callPreLoginEvents(offlineProfile);
                    } else {
                        log.warn("Failed to lookup profile for {}", getPlayerName(), cause);
                        disconnect("Failed to lookup profile");
                    }
                }
            });
        }
    }

    private void callPreLoginEvents(MinecraftProfile profile) {
        InetAddress ipAddress = this.netLoginHandler.getSocket().getInetAddress();
        AsyncPlayerPreLoginEvent asyncEvent = new AsyncPlayerPreLoginEvent(new PlayerProfileImpl(profile), ipAddress);
        asyncEvent.callEvent();
        
        if (asyncEvent.getResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            disconnect(asyncEvent.getKickMessage());
            return;
        }
        
        if (PlayerPreLoginEvent.getHandlerList().getRegisteredListeners().length != 0) {
            PlayerPreLoginEvent event = new PlayerPreLoginEvent(profile.name(), ipAddress);
            CompletableFuture<PlayerPreLoginEvent.Result> future = new CompletableFuture<>();
            this.server.queueSyncTask(() -> {
                event.callEvent();
                future.complete(event.getResult());
            });

            try {
                if (future.get() != PlayerPreLoginEvent.Result.ALLOWED) {
                    disconnect(event.getKickMessage());
                    return;
                }
            } catch (InterruptedException | ExecutionException e) {
                log.warn("Failed to call sync {} for {}", event.getClass().getSimpleName(), getPlayerName(), e);
                disconnect("Internal server error");
                return;
            }
        }

        connectPlayer(profile);
    }

    private void connectPlayer(MinecraftProfile profile) {
        // TODO: kick player in case another player with their username or uuid is online

        log.info("UUID of player {} is {}", getPlayerName(), profile.id());
        NetLoginHandler.a(this.netLoginHandler, this.loginPacket);
    }

    private String getPlayerName() {
        return this.loginPacket.name;
    }

    private void disconnect(String message) {
        this.netLoginHandler.disconnect(message);
    }
}
