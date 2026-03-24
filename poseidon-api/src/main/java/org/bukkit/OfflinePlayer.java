package org.bukkit;

import org.bukkit.permissions.ServerOperator;

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
     * @return Player name
     */
    String getName();

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
