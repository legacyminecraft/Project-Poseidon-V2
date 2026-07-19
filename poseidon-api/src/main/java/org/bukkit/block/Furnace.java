package org.bukkit.block;

import com.legacyminecraft.poseidon.block.TileState;

/**
 * Represents a furnace.
 *
 * @author sk89q
 */
public interface Furnace extends TileState, ContainerBlock { // Poseidon - extends TileState

    /**
     * Get burn time.
     *
     * @return
     */
    short getBurnTime();

    /**
     * Set burn time.
     *
     * @param burnTime
     */
    void setBurnTime(short burnTime);

    /**
     * Get cook time.
     *
     * @return
     */
    short getCookTime();

    /**
     * Set cook time.
     *
     * @param cookTime
     */
    void setCookTime(short cookTime);
}
