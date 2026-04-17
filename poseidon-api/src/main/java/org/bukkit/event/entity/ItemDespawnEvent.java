package org.bukkit.event.entity;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when an item is about to be removed from the world.
 */
public class ItemDespawnEvent extends EntityEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Location location;

    private boolean cancelled;

    public ItemDespawnEvent(Item despawnee, Location location) {
        super(Type.FIXED_EVENT, despawnee);
        this.location = location;
    }

    @Override
    public Item getEntity() {
        return (Item) this.entity;
    }

    /**
     * Gets the location at which the item is despawning.
     *
     * @return The location at which the item is despawning
     */
    public Location getLocation() {
        return this.location;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
