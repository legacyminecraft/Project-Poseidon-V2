package com.legacyminecraft.poseidon.event.profile;

import com.legacyminecraft.poseidon.profile.PlayerProfile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * This event is never fired by the server. It is intended to be manually fired
 * in order for plugins using UUID-based storage to be able to update any data
 * associated with a player to reference a new UUID.
 * <p>
 * The primary use of this event is to facilitate migration of data from one
 * player to another.
 * <p>
 * One such use case would to be to migrate data of a player who had previously
 * been playing with an offline account and who purchased a premium account
 * with the same name and lost their data on the server as a result.
 */
public class PlayerProfileIdChangedEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final PlayerProfile profile;
    private final UUID oldId;

    public PlayerProfileIdChangedEvent(PlayerProfile profile, UUID oldId) {
        this.profile = profile;
        this.oldId = oldId;
    }

    /**
     * Returns the profile of the player whose UUID has changed.
     *
     * @return the profile
     */
    public PlayerProfile getProfile() {
        return this.profile;
    }

    /**
     * Returns the player's old UUID.
     *
     * @return the old UUID
     */
    public UUID getOldId() {
        return this.oldId;
    }

    /**
     * Returns the player's new UUID.
     *
     * @return the new UUID
     */
    public UUID getNewId() {
        return this.profile.getUniqueId();
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
