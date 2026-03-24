package org.bukkit.material;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;

/**
 * MaterialData for signs
 */
public class Sign extends MaterialData implements Attachable {
    public Sign() {
        super(Material.SIGN_POST);
    }

    public Sign(final int type) {
        super(type);
    }

    public Sign(final Material type) {
        super(type);
    }

    public Sign(final int type, final byte data) {
        super(type, data);
    }

    public Sign(final Material type, final byte data) {
        super(type, data);
    }

    /**
     * Check if this sign is attached to a wall
     *
     * @return true if this sign is attached to a wall, false if set on top of a
     *         block
     */
    public boolean isWallSign() {
        return getItemType() == Material.WALL_SIGN;
    }

    /**
     * Gets the face that this block is attached on
     *
     * @return BlockFace attached to
     */
    public BlockFace getAttachedFace() {
        if (isWallSign()) {
            byte data = getData();

            return switch (data) {
                case 0x2 -> BlockFace.WEST;
                case 0x3 -> BlockFace.EAST;
                case 0x4 -> BlockFace.SOUTH;
                case 0x5 -> BlockFace.NORTH;
                default -> null;
            };
        } else {
            return BlockFace.DOWN;
        }
    }

    /**
     * Gets the direction that this sign is currently facing
     *
     * @return BlockFace indicating where this sign is facing
     */
    public BlockFace getFacing() {
        byte data = getData();

        if (!isWallSign()) {
            return switch (data) {
                case 0x0 -> BlockFace.WEST;
                case 0x1 -> BlockFace.WEST_NORTH_WEST;
                case 0x2 -> BlockFace.NORTH_WEST;
                case 0x3 -> BlockFace.NORTH_NORTH_WEST;
                case 0x4 -> BlockFace.NORTH;
                case 0x5 -> BlockFace.NORTH_NORTH_EAST;
                case 0x6 -> BlockFace.NORTH_EAST;
                case 0x7 -> BlockFace.EAST_NORTH_EAST;
                case 0x8 -> BlockFace.EAST;
                case 0x9 -> BlockFace.EAST_SOUTH_EAST;
                case 0xA -> BlockFace.SOUTH_EAST;
                case 0xB -> BlockFace.SOUTH_SOUTH_EAST;
                case 0xC -> BlockFace.SOUTH;
                case 0xD -> BlockFace.SOUTH_SOUTH_WEST;
                case 0xE -> BlockFace.SOUTH_WEST;
                case 0xF -> BlockFace.WEST_SOUTH_WEST;
                default -> null;
            };
        } else {
            return getAttachedFace().getOppositeFace();
        }
    }

    public void setFacingDirection(BlockFace face) {
        byte data;

        if (isWallSign()) {
            data = switch (face) {
                case EAST -> 0x2;
                case WEST -> 0x3;
                case NORTH -> 0x4;
                default -> 0x5;
            };
        } else {
            data = switch (face) {
                case WEST -> 0x0;
                case WEST_NORTH_WEST -> 0x1;
                case NORTH_WEST -> 0x2;
                case NORTH_NORTH_WEST -> 0x3;
                case NORTH -> 0x4;
                case NORTH_NORTH_EAST -> 0x5;
                case NORTH_EAST -> 0x6;
                case EAST_NORTH_EAST -> 0x7;
                case EAST -> 0x8;
                case EAST_SOUTH_EAST -> 0x9;
                case SOUTH_EAST -> 0xA;
                case SOUTH_SOUTH_EAST -> 0xB;
                case SOUTH -> 0xC;
                case SOUTH_SOUTH_WEST -> 0xD;
                case WEST_SOUTH_WEST -> 0xF;
                default -> 0xE;
            };
        }

        setData(data);
    }

    @Override
    public String toString() {
        return super.toString() + " facing " + getFacing();
    }
}
