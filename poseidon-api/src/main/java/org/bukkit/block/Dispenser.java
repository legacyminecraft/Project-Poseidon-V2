package org.bukkit.block;

import com.legacyminecraft.poseidon.block.TileState;

/**
 * Represents a dispenser.
 *
 * @author sk89q
 */
public interface Dispenser extends TileState, ContainerBlock { // Poseidon - extends TileState

    /**
     * Attempts to dispense the contents of this block<br />
     * <br />
     * If the block is no longer a dispenser, this will return false
     *
     * @return true if successful, otherwise false
     */
    boolean dispense();
}
