package org.bukkit.event.player;

import com.legacyminecraft.poseidon.profile.PlayerProfile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.net.InetAddress;
import java.util.UUID;

/**
 * Stores details for players attempting to log in.
 * <p>
 * This event is asynchronous, and not run on the main thread.
 * <p>
 * This event is fired after the server has successfully completed
 * Mojang authentication. The event is still fired if the server is in offline mode.
 */
public class AsyncPlayerPreLoginEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final PlayerProfile profile;
    private final InetAddress ipAddress;
    private final InetAddress rawAddress;

    private Result result = Result.ALLOWED;
    private String message = "";

    public AsyncPlayerPreLoginEvent(PlayerProfile profile, InetAddress ipAddress, InetAddress rawAddress) {
        super(true);
        this.profile = profile;
        this.ipAddress = ipAddress;
        this.rawAddress = rawAddress;
    }

    /**
     * Gets the current result of the login, as an enum
     *
     * @return Current Result of the login
     */
    public Result getResult() {
        return this.result;
    }

    /**
     * Sets the new result of the login, as an enum
     *
     * @param result New result to set
     */
    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * Gets the current kick message that will be used if getResult() != Result.ALLOWED
     *
     * @return Current kick message
     */
    public String getKickMessage() {
        return this.message;
    }

    /**
     * Sets the kick message to display if getResult() != Result.ALLOWED
     *
     * @param message New kick message
     */
    public void setKickMessage(String message) {
        this.message = message;
    }

    /**
     * Allows the player to log in
     */
    public void allow() {
        this.result = Result.ALLOWED;
        this.message = "";
    }

    /**
     * Disallows the player from logging in, with the given reason
     *
     * @param result New result for disallowing the player
     * @param message Kick message to display to the user
     */
    public void disallow(Result result, String message) {
        this.result = result;
        this.message = message;
    }

    /**
     * Gets the player's name.
     *
     * @return the player's name
     */
    public String getName() {
        return this.profile.getName();
    }

    /**
     * Gets the player's UUID.
     *
     * @return the player's UUID
     */
    public UUID getUniqueId() {
        return this.profile.getUniqueId();
    }

    /**
     * Gets the profile of the player logging in
     *
     * @return the profile
     */
    public PlayerProfile getPlayerProfile() {
        return this.profile;
    }

    /**
     * Gets the player IP address
     *
     * @return the IP address
     */
    public InetAddress getAddress() {
        return this.ipAddress;
    }

    /**
     * Gets the raw address of the player logging in
     *
     * @return the raw address
     */
    public InetAddress getRawAddress() {
        return this.rawAddress;
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
