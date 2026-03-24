package org.bukkit.material;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;

/**
 * Represents stairs.
 */
public class Stairs extends MaterialData implements Directional {

    public Stairs(final int type) {
        super(type);
    }

    public Stairs(final Material type) {
        super(type);
    }

    public Stairs(final int type, final byte data) {
        super(type, data);
    }

    public Stairs(final Material type, final byte data) {
        super(type, data);
    }

    /**
     * @return the direction the stairs ascend towards
     */
    public BlockFace getAscendingDirection() {
        byte data = getData();

        return switch (data) {
            case 0x1 -> BlockFace.NORTH;
            case 0x2 -> BlockFace.WEST;
            case 0x3 -> BlockFace.EAST;
            default -> BlockFace.SOUTH;
        };
    }

    /**
     * @return the direction the stairs descend towards
     */
    public BlockFace getDescendingDirection() {
        return getAscendingDirection().getOppositeFace();
    }

    /**
     * Set the direction the stair part of the block is facing
     */
    public void setFacingDirection(BlockFace face) {
        byte data = switch (face) {
            case SOUTH -> 0x1;
            case EAST -> 0x2;
            case WEST -> 0x3;
            default -> 0x0;
        };

        setData(data);
    }

    /**
     * @return the direction the stair part of the block is facing
     */
    public BlockFace getFacing() {
        return getDescendingDirection();
    }

    @Override
    public String toString() {
        return super.toString() + " facing " + getFacing();
    }
}
