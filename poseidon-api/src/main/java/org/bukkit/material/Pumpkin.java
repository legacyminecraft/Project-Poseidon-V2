package org.bukkit.material;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;

/**
 * Represents a pumpkin.
 */
public class Pumpkin extends MaterialData implements Directional {

    public Pumpkin() {
        super(Material.PUMPKIN);
    }

    /**
     * Instantiate a pumpkin facing in a particular direction.
     * @param direction the direction the pumkin's face is facing
     */
    public Pumpkin(BlockFace direction) {
        this();
        setFacingDirection(direction);
    }

    public Pumpkin(final int type) {
        super(type);
    }

    public Pumpkin(final Material type) {
        super(type);
    }

    public Pumpkin(final int type, final byte data) {
        super(type, data);
    }

    public Pumpkin(final Material type, final byte data) {
        super(type, data);
    }

    public boolean isLit() {
        return getItemType() == Material.JACK_O_LANTERN;
    }

    public void setFacingDirection(BlockFace face) {
        byte data = switch (face) {
            case EAST -> 0x0;
            case SOUTH -> 0x1;
            case WEST -> 0x2;
            default -> 0x3;
        };

        setData(data);
    }

    public BlockFace getFacing() {
        byte data = getData();

        return switch (data) {
            case 0x0 -> BlockFace.EAST;
            case 0x1 -> BlockFace.SOUTH;
            case 0x2 -> BlockFace.WEST;
            default -> BlockFace.SOUTH;
        };
    }

    @Override
    public String toString() {
        return super.toString() + " facing " + getFacing() + " " + (isLit() ? "" : "NOT ") + "LIT";
    }
}
