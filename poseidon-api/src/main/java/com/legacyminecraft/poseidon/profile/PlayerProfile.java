package com.legacyminecraft.poseidon.profile;

import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * Represents a player profile, including the player's UUID and name.
 */
public interface PlayerProfile {

    /**
     * Returns the player's name, if set
     *
     * @return the player's name, if set
     */
    @Nullable String getName();

    /**
     * Returns the player's UUID
     *
     * @return the player's UUID
     */
    UUID getUniqueId();
}
