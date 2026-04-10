package com.projectposeidon.api;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * This class is provided for backward compatibility of plugins using the
 * legacy methods of accessing cached player UUIDs by name and vice versa.
 *
 * @deprecated It is encouraged to use
 * {@link Server#getOfflinePlayerIfCached(String)} and
 * {@link Server#getOfflinePlayer(UUID)} to retrieve offline players
 * by name and UUID, and to use {@link OfflinePlayer#getPlayerProfile()} to get
 * an instance of the player's profile, which contains the UUID, name and
 * online mode status of a player.
 */
@Deprecated
public final class PoseidonUUID {

    private PoseidonUUID() {
    }

    /**
     * @param username username of a player who has connected
     * @return a Mojang UUID if known, otherwise null
     */
    @Deprecated
    public static @Nullable UUID getPlayerMojangUUID(String username) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(username);
        if (offlinePlayer != null && offlinePlayer.getPlayerProfile().isOnlineMode()) {
            return offlinePlayer.getUniqueId();
        }
        return null;
    }

    /**
     * @param username username of a player who has connected
     * @return a Mojang UUID if known, otherwise an offline UUID
     */
    @Deprecated
    public static UUID getPlayerGracefulUUID(String username) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(username);
        if (offlinePlayer != null) {
            return offlinePlayer.getUniqueId();
        }
        return Bukkit.createOfflineProfile(username).getUniqueId();
    }

    /**
     * Gets a UUID of a player if they have joined before.
     *
     * @param username username of a player who has connected
     * @param onlineUUID whether to search for online or offline UUIDs
     * @return a UUID if known in cache, otherwise null
     */
    @Deprecated
    public static @Nullable UUID getPlayerUUIDFromCache(String username, boolean onlineUUID) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(username);
        if (offlinePlayer != null && offlinePlayer.getPlayerProfile().isOnlineMode() == onlineUUID) {
            return offlinePlayer.getUniqueId();
        }
        return null;
    }

    /**
     * @param username username of a player
     * @return an offline UUID for a player
     */
    @Deprecated
    public static UUID getPlayerOfflineUUID(String username) {
        return Bukkit.createOfflineProfile(username).getUniqueId();
    }

    /**
     * @param username username of a player
     * @return a {@link UUIDType} enum.
     */
    @Deprecated
    public static UUIDType getPlayerUUIDCacheStatus(String username) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(username);
        if (offlinePlayer != null) {
            return offlinePlayer.getPlayerProfile().isOnlineMode() ? UUIDType.ONLINE : UUIDType.OFFLINE;
        }
        return UUIDType.UNKNOWN;
    }

    /**
     * @param uuid UUID for a player
     * @return a corresponding username if known, otherwise null
     */
    @Deprecated
    public static @Nullable String getPlayerUsernameFromUUID(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        return offlinePlayer.getPlayerProfile().getName();
    }
}
