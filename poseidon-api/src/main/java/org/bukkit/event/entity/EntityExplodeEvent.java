package org.bukkit.event.entity;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import java.util.Set;

/**
 * Called when an entity explodes
 */
public class EntityExplodeEvent extends EntityEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private boolean cancel;
    private Location location;
    private Set<Block> blocks;
    private float yield = 0.3F;

    public EntityExplodeEvent(Entity what, Location location, Set<Block> blocks) {
        super(Type.ENTITY_EXPLODE, what);
        this.location = location;
        this.cancel = false;
        this.blocks = blocks;
    }

    /**
     * Returns the list of blocks that would have been removed or were
     * removed from the explosion event.
     */
    public Set<Block> blockList() {
        return blocks;
    }

    /**
     * Returns the location where the explosion happened.
     * It is not possible to get this value from the Entity as
     * the Entity no longer exists in the world.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Returns the percentage of blocks to drop from this explosion
     *
     * @return
     */
    public float getYield() {
        return yield;
    }

    /**
     * Sets the percentage of blocks to drop from this explosion
     */
    public void setYield(float yield) {
        this.yield = yield;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
