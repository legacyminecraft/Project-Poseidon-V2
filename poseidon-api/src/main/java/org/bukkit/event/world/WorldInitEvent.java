package org.bukkit.event.world;

import org.bukkit.World;
import org.bukkit.event.HandlerList;

/**
 * Called when a World is initializing
 */
public class WorldInitEvent extends WorldEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public WorldInitEvent(World world) {
        super(Type.WORLD_INIT, world);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
