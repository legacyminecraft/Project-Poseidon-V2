package org.bukkit.craftbukkit;

import com.legacyminecraft.poseidon.profile.MinecraftProfile;
import com.legacyminecraft.poseidon.profile.PlayerProfile;
import com.legacyminecraft.poseidon.profile.PlayerProfileImpl;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class CraftOfflinePlayer implements OfflinePlayer {

    private final MinecraftProfile profile; // Poseidon - replace name with profile
    private final CraftServer server;

    // Poseidon - change signature
    protected CraftOfflinePlayer(CraftServer server, MinecraftProfile profile) {
        this.server = server;
        this.profile = profile;
    }

    public boolean isOnline() {
        return getPlayer() != null; // Poseidon
    }

    // Poseidon start
    public @Nullable String getName() {
        Player player = getPlayer();
        if (player != null) {
            return player.getName();
        }

        MinecraftProfile profile = this.profile;
        if (profile.name() != null && !profile.name().isBlank()) {
            return profile.name();
        }

        return null;
    }

    public UUID getUniqueId() {
        return this.profile.id();
    }

    public PlayerProfile getPlayerProfile() {
        return new PlayerProfileImpl(this.profile);
    }

    public @Nullable Player getPlayer() {
        return this.server.getPlayer(getUniqueId());
    }
    // Poseidon end

    public Server getServer() {
        return server;
    }

    public boolean isOp() {
        return server.getHandle().isOp(getName().toLowerCase());
    }

    public void setOp(boolean value) {
        if (value == isOp()) return;

        if (value) {
            server.getHandle().e(getName().toLowerCase());
        } else {
            server.getHandle().f(getName().toLowerCase());
        }
    }

    public boolean isBanned() {
        return server.getHandle().banByName.contains(getName().toLowerCase());
    }

    public void setBanned(boolean value) {
        if (value) {
            server.getHandle().a(getName().toLowerCase());
        } else {
            server.getHandle().b(getName().toLowerCase());
        }
    }

    public boolean isWhitelisted() {
        return server.getHandle().e().contains(getName().toLowerCase());
    }

    public void setWhitelisted(boolean value) {
        if (value) {
            server.getHandle().k(getName().toLowerCase());
        } else {
            server.getHandle().l(getName().toLowerCase());
        }
    }
}
