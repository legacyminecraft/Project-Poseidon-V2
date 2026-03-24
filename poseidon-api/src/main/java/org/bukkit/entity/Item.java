package org.bukkit.entity;

import org.bukkit.inventory.ItemStack;

/**
 * Represents an Item.
 *
 * @author Cogito
 *
 */
public interface Item extends Entity {

    /**
     * Gets the item stack associated with this item drop.
     *
     * @return
     */
    ItemStack getItemStack();

    /**
     * Sets the item stack associated with this item drop.
     *
     * @param stack
     */
    void setItemStack(ItemStack stack);
}
