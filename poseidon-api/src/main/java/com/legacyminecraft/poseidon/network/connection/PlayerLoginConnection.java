package com.legacyminecraft.poseidon.network.connection;

import com.legacyminecraft.poseidon.profile.PlayerProfile;
import org.jspecify.annotations.Nullable;

/**
 * Represents a player's connection during the login process.
 */
public interface PlayerLoginConnection extends PlayerConnection {

    /**
     * Returns the player's profile.
     *
     * @return the profile, or null if it has not been determined yet
     */
    @Nullable PlayerProfile getProfile();
}
