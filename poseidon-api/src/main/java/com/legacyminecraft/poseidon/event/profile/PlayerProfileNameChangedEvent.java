package com.legacyminecraft.poseidon.event.profile;

import com.legacyminecraft.poseidon.profile.PlayerProfile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is fired when the server detects that a player has changed their
 * name. It is fired when the player has successfully logged in.
 * <p>
 * The primary use of this event is for plugins using name-based storage to be
 * able to update any data associated with a player to reference the new name.
 * As such, this event may also be fired manually to facilitate data migration.
 * <p>
 * <b>Note:</b> This event will also fire when merely the casing of a player's
 * name has changed, meaning it will also fire for players with offline
 * profiles, given that the casing of the name has changed.
 */
public class PlayerProfileNameChangedEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final PlayerProfile profile;
    private final String oldName;

    public PlayerProfileNameChangedEvent(PlayerProfile profile, String oldName) {
        this.profile = profile;
        this.oldName = oldName;
    }

    /**
     * Returns the profile of the player whose name has changed.
     *
     * @return the profile
     */
    public PlayerProfile getProfile() {
        return this.profile;
    }

    /**
     * Returns the player's old name.
     *
     * @return the old name
     */
    public String getOldName() {
        return this.oldName;
    }

    /**
     * Returns the player's new name.
     *
     * @return the new name
     */
    public String getNewName() {
        return this.profile.getName();
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
