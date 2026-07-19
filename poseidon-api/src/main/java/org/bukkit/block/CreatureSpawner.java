package org.bukkit.block;

import com.legacyminecraft.poseidon.block.TileState;
import org.bukkit.entity.CreatureType;
import org.jspecify.annotations.Nullable;

/**
 * Represents a creature spawner.
 *
 * @author sk89q
 * @author Cogito
 */
public interface CreatureSpawner extends TileState { // Poseidon - extends TileState

    /**
     * Get the spawner's creature type.
     *
     * @return
     */
    @Nullable CreatureType getCreatureType();

    /**
     * Set the spawner creature type.
     *
     * @param mobType
     */
    void setCreatureType(CreatureType creatureType);

    /**
     * Get the spawner's creature type.
     *
     * @return
     */
    String getCreatureTypeId();

    /**
     * Set the spawner mob type.
     *
     * @param creatureType
     */
    void setCreatureTypeId(String creatureType);

    /**
     * Get the spawner's delay.
     *
     * @return
     */
    int getDelay();

    /**
     * Set the spawner's delay.
     *
     * @param delay
     */
    void setDelay(int delay);
}
