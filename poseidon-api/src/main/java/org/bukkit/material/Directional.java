package org.bukkit.material;

import org.bukkit.block.BlockFace;

public interface Directional {

    /**
     * Sets the direction that this block is facing in
     */
    void setFacingDirection(BlockFace face);

    /**
     * Gets the direction this block is facing
     *
     * @return the direction this block is facing
     */
    BlockFace getFacing();
}
