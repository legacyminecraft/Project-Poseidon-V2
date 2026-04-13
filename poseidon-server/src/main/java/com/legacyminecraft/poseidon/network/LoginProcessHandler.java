package com.legacyminecraft.poseidon.network;

import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.profile.MinecraftProfile;
import com.legacyminecraft.poseidon.profile.PlayerProfileImpl;
import com.legacyminecraft.poseidon.profile.ProfileNotFoundException;
import com.legacyminecraft.poseidon.service.ServiceClientException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetLoginHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public final class LoginProcessHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(LoginProcessHandler.class);

    private final MinecraftServer server;
    private final NetLoginHandler netLoginHandler;
    private final String name;

    public LoginProcessHandler(MinecraftServer server, NetLoginHandler netLoginHandler, String name) {
        this.server = server;
        this.netLoginHandler = netLoginHandler;
        this.name = name;
    }

    @Override
    public void run() {
        validateName();
    }

    private void validateName() {
        if (Poseidon.getConfig().nameValidation.enabled) {
            int minLength = Poseidon.getConfig().nameValidation.minimumLength;
            int maxLength = Poseidon.getConfig().nameValidation.maximumLength;
            Pattern allowedChars = Poseidon.getConfig().nameValidation.allowedCharacters;
            if (this.name.length() < minLength) {
                disconnect("Name too short, minimum " + minLength + " characters allowed");
                return;
            } else if (this.name.length() > maxLength) {
                disconnect("Name too long, maximum " + maxLength + " characters allowed");
                return;
            } else if (!allowedChars.matcher(this.name).matches()) {
                disconnect("Name has invalid characters, allowed characters: " + allowedChars);
                return;
            }
        }

        verifySession();
    }

    private void verifySession() {
        if (!this.server.onlineMode) {
            getPlayerProfile();
        } else {
            try {
                String name = this.name;
                String serverId = NetLoginHandler.a(this.netLoginHandler);
                InetAddress ipAddress = this.netLoginHandler.getSocket().getInetAddress();
                if (Poseidon.getSessionService().verifySession(name, serverId, ipAddress)) {
                    getPlayerProfile();
                } else {
                    log.info("{} tried to login with an invalid session", this.name);
                    disconnect("Invalid session");
                }
            } catch (ServiceClientException e) {
                log.warn("Failed to verify session for {}", this.name, e);
                disconnect("Failed to verify session");
            }
        }
    }

    private void getPlayerProfile() {
        MinecraftProfile profile;
        Optional<MinecraftProfile> optional = Poseidon.getProfileCache().getProfile(this.name, true);
        if (optional.isPresent() && optional.get().onlineMode()) {
            profile = optional.get();
        } else {
            try {
                profile = Poseidon.getProfileService().lookupProfileByName(this.name);
            } catch (ProfileNotFoundException e) {
                if (Poseidon.getConfig().profiles.allowOfflineProfiles) {
                    profile = MinecraftProfile.createOffline(this.name);
                } else {
                    log.info("Disconnecting {} as they do not have an online profile and offline profiles are disallowed.", this.name);
                    disconnect("Offline accounts are not supported");
                    return;
                }
            } catch (ServiceClientException e) {
                log.warn("Failed to lookup profile for {}", this.name, e);
                disconnect("Failed to lookup profile");
                return;
            }
        }

        log.info("UUID of player {} is {}", this.name, profile.id());
        Poseidon.getProfileCache().addProfile(profile);
        callPreLoginEvents(profile);
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
                log.warn("Failed to call sync {} for {}", event.getClass().getSimpleName(), this.name, e);
                disconnect("Internal server error");
                return;
            }
        }

        loginPlayer(profile);
    }

    private void loginPlayer(MinecraftProfile profile) {
        if (profile.onlineMode()) {
            loginOnlineProfile(profile);
        } else {
            loginOfflineProfile(profile);
        }
    }

    private void loginOnlineProfile(MinecraftProfile profile) {
        if (this.name.equals(profile.name())) {
            finishLogin(profile);
        } else {
            switch (Poseidon.getConfig().profiles.handleLoginsWithWrongNameCasing) {
                case KEEP -> {
                    MinecraftProfile newProfile = new MinecraftProfile(profile.id(), this.name, profile.onlineMode());
                    finishLogin(newProfile);
                }
                case CORRECT -> finishLogin(profile);
                case REJECT -> {
                    log.info("Disconnecting {} as the correct name is '{}' and wrongly cased names should be rejected.", this.name, profile.name());
                    disconnect("Invalid name '" + this.name + "', correct name is '" + profile.name() + "'");
                }
            }
        }
    }

    private void loginOfflineProfile(MinecraftProfile profile) {
        finishLogin(profile);
    }

    private void finishLogin(MinecraftProfile profile) {
        NetLoginHandler.a(this.netLoginHandler, profile);
    }

    private void disconnect(String message) {
        this.netLoginHandler.disconnect(message);
    }
}
