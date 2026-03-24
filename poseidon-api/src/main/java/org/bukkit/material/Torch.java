package org.bukkit.material;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;

/**
 * MaterialData for torches
 */
public class Torch extends SimpleAttachableMaterialData {
    public Torch() {
        super(Material.TORCH);
    }

    public Torch(final int type) {
        super(type);
    }

    public Torch(final Material type) {
        super(type);
    }

    public Torch(final int type, final byte data) {
        super(type, data);
    }

    public Torch(final Material type, final byte data) {
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
            case 0x1 -> BlockFace.NORTH;
            case 0x2 -> BlockFace.SOUTH;
            case 0x3 -> BlockFace.EAST;
            case 0x4 -> BlockFace.WEST;
            case 0x5 -> BlockFace.DOWN;
            default -> null;
        };
    }

    public void setFacingDirection(BlockFace face) {
        byte data = switch (face) {
            case SOUTH -> 0x1;
            case NORTH -> 0x2;
            case WEST -> 0x3;
            case EAST -> 0x4;
            default -> 0x5;
        };

        setData(data);
    }
}
