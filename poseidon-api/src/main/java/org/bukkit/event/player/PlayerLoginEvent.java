package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.net.InetAddress;

/**
 * Stores details for players attempting to log in
 */
public class PlayerLoginEvent extends PlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final InetAddress ipAddress;

    private Result result;
    private String message;

    public PlayerLoginEvent(final Player player, final InetAddress ipAddress) {
        super(Type.PLAYER_LOGIN, player);
        this.ipAddress = ipAddress;
        this.result = Result.ALLOWED;
        this.message = "";
    }

    public PlayerLoginEvent(final Type type, final Player player, final InetAddress ipAddress, final Result result, final String message) {
        super(type, player);
        this.ipAddress = ipAddress;
        this.result = result;
        this.message = message;
    }

    // Poseidon start
    /**
     * Gets the player IP address
     *
     * @return the IP address
     */
    public InetAddress getAddress() {
        return this.ipAddress;
    }
    // Poseidon end

    /**
     * Gets the current result of the login, as an enum
     *
     * @return Current Result of the login
     */
    public Result getResult() {
        return result;
    }

    /**
     * Sets the new result of the login, as an enum
     *
     * @param result New result to set
     */
    public void setResult(final Result result) {
        this.result = result;
    }

    /**
     * Gets the current kick message that will be used if getResult() != Result.ALLOWED
     *
     * @return Current kick message
     */
    public String getKickMessage() {
        return message;
    }

    /**
     * Sets the kick message to display if getResult() != Result.ALLOWED
     *
     * @param message New kick message
     */
    public void setKickMessage(final String message) {
        this.message = message;
    }

    /**
     * Allows the player to log in
     */
    public void allow() {
        result = Result.ALLOWED;
        message = "";
    }

    /**
     * Disallows the player from logging in, with the given reason
     *
     * @param result New result for disallowing the player
     * @param message Kick message to display to the user
     */
    public void disallow(final Result result, final String message) {
        this.result = result;
        this.message = message;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * Basic kick reasons for communicating to plugins
     */
    public enum Result {

        /**
         * The player is allowed to log in
         */
        ALLOWED,
        /**
         * The player is not allowed to log in, due to the server being full
         */
        KICK_FULL,
        /**
         * The player is not allowed to log in, due to them being banned
         */
        KICK_BANNED,
        /**
         * The player is not allowed to log in, due to them not being on the white list
         */
        KICK_WHITELIST,
        /**
         * The player is not allowed to log in, for reasons undefined
         */
        KICK_OTHER
    }
}
