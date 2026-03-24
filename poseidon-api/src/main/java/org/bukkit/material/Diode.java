package org.bukkit.material;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class Diode extends MaterialData implements Directional {
    public Diode() {
        super(Material.DIODE_BLOCK_ON);
    }

    public Diode(int type) {
        super(type);
    }

    public Diode(Material type) {
        super(type);
    }

    public Diode(int type, byte data) {
        super(type, data);
    }

    public Diode(Material type, byte data) {
        super(type, data);
    }

    /**
     * Sets the delay of the repeater
     *
     * @param delay
     *            The new delay (1-4)
     */
    public void setDelay(int delay) {
        if (delay > 4) {
            delay = 4;
        }
        if (delay < 1) {
            delay = 1;
        }
        byte newData = (byte) (getData() & 0x3);

        setData((byte) (newData | ((delay - 1) << 2)));
    }

    /**
     * Gets the delay of the repeater in ticks
     *
     * @return The delay (1-4)
     */
    public int getDelay() {
        return (getData() >> 2) + 1;
    }

    public void setFacingDirection(BlockFace face) {
        int delay = getDelay();
        byte data = switch (face) {
            case SOUTH -> 0x1;
            case WEST -> 0x2;
            case NORTH -> 0x3;
            default -> 0x0;
        };

        setData(data);
        setDelay(delay);
    }

    public BlockFace getFacing() {
        byte data = (byte) (getData() & 0x3);

        return switch (data) {
            case 0x1 -> BlockFace.SOUTH;
            case 0x2 -> BlockFace.WEST;
            case 0x3 -> BlockFace.NORTH;
            default -> BlockFace.EAST;
        };
    }

    @Override
    public String toString() {
        return super.toString() + " facing " + getFacing() + " with " + getDelay() + " ticks delay";
    }
}
