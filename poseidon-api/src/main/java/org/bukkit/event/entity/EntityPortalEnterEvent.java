package org.bukkit.event.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;

/**
 * Stores data for entities standing inside a portal block
 */
public class EntityPortalEnterEvent extends EntityEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private Location location;

    public EntityPortalEnterEvent(Entity entity, Location location) {
        super(Type.ENTITY_PORTAL_ENTER, entity);
        this.location = location;
    }

    /**
     * Gets the portal block the entity is touching
     *
     * @return The portal block the entity is touching
     */
    public Location getLocation() {
        return location;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
