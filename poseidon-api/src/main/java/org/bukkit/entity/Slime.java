/**
 *
 */
package org.bukkit.entity;

/**
 * Represents a Slime.
 *
 * @author Cogito
 *
 */
public interface Slime extends LivingEntity {

    /**
     * @author Celtic Minstrel
     * @return The size of the slime
     */
    int getSize();

    /**
     * @author Celtic Minstrel
     * @param sz The new size of the slime.
     */
    void setSize(int sz);
}
