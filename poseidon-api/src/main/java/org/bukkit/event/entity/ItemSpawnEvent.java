package org.bukkit.event.entity;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when an item is spawned into a world
 */
public class ItemSpawnEvent extends EntityEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private Location location;
    private boolean canceled;

    public ItemSpawnEvent(Item spawnee, Location loc) {
        super(Type.ITEM_SPAWN, spawnee);
        this.location = loc;
    }

    // Poseidon start
    @Override
    public Item getEntity() {
        return (Item) this.entity;
    }
    // Poseidon end

    /**
     * Gets the location at which the item is spawning.
     *
     * @return The location at which the item is spawning
     */
    public Location getLocation() {
        return location;
    }

    public boolean isCancelled() {
        return canceled;
    }

    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
