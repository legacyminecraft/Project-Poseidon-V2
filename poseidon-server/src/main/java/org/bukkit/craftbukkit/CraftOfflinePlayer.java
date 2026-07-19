package org.bukkit.craftbukkit;

import com.legacyminecraft.poseidon.persistence.PersistentDataContainer;
import com.legacyminecraft.poseidon.persistence.PersistentDataContainerImpl;
import com.legacyminecraft.poseidon.profile.MinecraftProfile;
import com.legacyminecraft.poseidon.profile.PlayerProfile;
import com.legacyminecraft.poseidon.profile.PlayerProfileImpl;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.PlayerNBTManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class CraftOfflinePlayer implements OfflinePlayer {

    private final MinecraftProfile profile; // Poseidon - replace name with profile
    private final CraftServer server;

    // Poseidon start
    private final PlayerNBTManager storage;
    private @Nullable NBTTagCompound currentData;
    // Poseidon end

    // Poseidon - change signature
    protected CraftOfflinePlayer(CraftServer server, MinecraftProfile profile) {
        this.server = server;
        this.profile = profile;
        this.storage = (PlayerNBTManager) server.getServer().worlds.get(0).p(); // Poseidon
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

    // Poseidon start
    public @Nullable NBTTagCompound getData() {
        return this.storage.a(this.profile);
    }

    private void saveData(NBTTagCompound compound) {
        this.storage.saveData(this.profile.name(), this.profile.id(), compound);
    }
    // Poseidon end

    // Poseidon start - PersistentDataContainer API
    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        throw new UnsupportedOperationException("Cannot call getPersistentDataContainer() on OfflinePlayer." +
                " Use accessPersistentDataContainer() or accessPersistentDataContainerReturning() instead.");
    }

    @Override
    public void accessPersistentDataContainer(Consumer<PersistentDataContainer> consumer) {
        accessPersistentDataContainerReturning(container -> {
            consumer.accept(container);
            return true;
        });
    }

    @Override
    public <T> T accessPersistentDataContainerReturning(Function<PersistentDataContainer, T> function) {
        Player player = getPlayer();
        if (player != null) {
            return player.accessPersistentDataContainerReturning(function);
        } else {
            boolean nestedAccess = this.currentData != null;
            if (!nestedAccess) {
                this.currentData = Optional.ofNullable(getData()).orElse(new NBTTagCompound());
            }

            NBTTagCompound compound = this.currentData.k(PersistentDataContainerImpl.TAG_KEY);
            if (!this.currentData.hasKey(PersistentDataContainerImpl.TAG_KEY)) {
                this.currentData.a(PersistentDataContainerImpl.TAG_KEY, compound);
            }

            T result = function.apply(new PersistentDataContainerImpl(compound));
            if (!nestedAccess) {
                saveData(this.currentData);
            }

            return result;
        }
    }
    // Poseidon end
}
