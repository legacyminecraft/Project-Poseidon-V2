package org.bukkit.event.entity;

import org.bukkit.entity.Projectile;
import org.bukkit.event.HandlerList;

/**
 * Called when a projectile hits an object
 */
public class ProjectileHitEvent extends EntityEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public ProjectileHitEvent(Projectile projectile) {
        super(Type.PROJECTILE_HIT, projectile);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
