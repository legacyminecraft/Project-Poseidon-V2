package org.bukkit.material;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;

/**
 * Represents a furnace or a dispenser.
 */
public class FurnaceAndDispenser extends MaterialData implements Directional {
    public FurnaceAndDispenser(final int type) {
        super(type);
    }

    public FurnaceAndDispenser(final Material type) {
        super(type);
    }

    public FurnaceAndDispenser(final int type, final byte data) {
        super(type, data);
    }

    public FurnaceAndDispenser(final Material type, final byte data) {
        super(type, data);
    }

    public void setFacingDirection(BlockFace face) {
        byte data = switch (face) {
            case EAST -> 0x2;
            case WEST -> 0x3;
            case NORTH -> 0x4;
            default -> 0x5;
        };

        setData(data);
    }

    public BlockFace getFacing() {
        byte data = getData();

        return switch (data) {
            case 0x2 -> BlockFace.EAST;
            case 0x3 -> BlockFace.WEST;
            case 0x4 -> BlockFace.NORTH;
            default -> BlockFace.SOUTH;
        };
    }

    @Override
    public String toString() {
        return super.toString() + " facing " + getFacing();
    }
}
