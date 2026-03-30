package org.bukkit.event.block;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;

public class BlockPistonRetractEvent extends BlockPistonEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public BlockPistonRetractEvent(Block block) {
        super(Type.BLOCK_PISTON_RETRACT, block);
    }

    /**
     * Gets the location where the possible moving block might be if the retracting
     * piston is sticky.
     *
     * @return The possible location of the possibly moving block.
     */
    public Location getRetractLocation() {
        return getBlock().getRelative(getDirection(), 2).getLocation();
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
