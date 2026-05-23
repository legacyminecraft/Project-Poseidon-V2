package org.bukkit;

import com.legacyminecraft.poseidon.profile.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public interface OfflinePlayer extends ServerOperator {

    /**
     * Checks if this player is currently online
     *
     * @return true if they are online
     */
    boolean isOnline();

    /**
     * Returns the name of this player
     *
     * @return Player name or null if we have not seen a name for this player yet
     */
    @Nullable String getName();

    // Poseidon start - profile API
    /**
     * Returns the UUID of this player
     *
     * @return Player UUID
     */
    UUID getUniqueId();

    /**
     * Returns a copy of this player's profile
     *
     * @return the player's profile
     */
    PlayerProfile getPlayerProfile();

    /**
     * Gets a {@link Player} object that this represents, if there is one
     * <p>
     * If the player is online, this will return that player. Otherwise,
     * it will return null.
     *
     * @return Online player
     */
    @Nullable
    Player getPlayer();
    // Poseidon end

    /**
     * Checks if this player is banned or not
     *
     * @return true if banned, otherwise false
     */
    boolean isBanned();

    /**
     * Bans or unbans this player
     *
     * @param banned true if banned
     */
    void setBanned(boolean banned);

    /**
     * Checks if this player is whitelisted or not
     *
     * @return true if whitelisted
     */
    boolean isWhitelisted();

    /**
     * Sets if this player is whitelisted or not
     *
     * @param value true if whitelisted
     */
    void setWhitelisted(boolean value);
}
