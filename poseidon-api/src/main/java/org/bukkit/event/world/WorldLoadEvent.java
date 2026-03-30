package org.bukkit.event.world;

import org.bukkit.World;
import org.bukkit.event.HandlerList;

/**
 * Called when a World is loaded
 */
public class WorldLoadEvent extends WorldEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public WorldLoadEvent(World world) {
        super(Type.WORLD_LOAD, world);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
