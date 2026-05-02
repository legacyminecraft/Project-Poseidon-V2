package com.legacyminecraft.poseidon.network.connection;

import org.bukkit.entity.Player;

/**
 * Represents a player's connection during gameplay.
 */
public interface PlayerGameConnection extends PlayerConnection {

    /**
     * Returns the player associated with this connection.
     *
     * @return the player
     */
    Player getPlayer();
}
