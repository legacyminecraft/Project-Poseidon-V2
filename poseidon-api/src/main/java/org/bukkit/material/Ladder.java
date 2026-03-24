package org.bukkit.material;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;

/**
 * Represents Ladder data
 */
public class Ladder extends SimpleAttachableMaterialData {
    public Ladder() {
        super(Material.LADDER);
    }

    public Ladder(final int type) {
        super(type);
    }

    public Ladder(final Material type) {
        super(type);
    }

    public Ladder(final int type, final byte data) {
        super(type, data);
    }

    public Ladder(final Material type, final byte data) {
        super(type, data);
    }

    /**
     * Gets the face that this block is attached on
     *
     * @return BlockFace attached to
     */
    public BlockFace getAttachedFace() {
        byte data = getData();

        return switch (data) {
            case 0x2 -> BlockFace.WEST;
            case 0x3 -> BlockFace.EAST;
            case 0x4 -> BlockFace.SOUTH;
            case 0x5 -> BlockFace.NORTH;
            default -> null;
        };
    }

    /**
     * Sets the direction this ladder is facing
     */
    public void setFacingDirection(BlockFace face) {
        byte data = switch (face) {
            case WEST -> 0x2;
            case EAST -> 0x3;
            case SOUTH -> 0x4;
            case NORTH -> 0x5;
            default -> 0x0;
        };

        setData(data);
    }
}
