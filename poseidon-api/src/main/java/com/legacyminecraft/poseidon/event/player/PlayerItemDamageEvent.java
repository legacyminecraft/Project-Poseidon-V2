package com.legacyminecraft.poseidon.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Called when an item used by the player takes durability damage as a result
 * of being used.
 */
public class PlayerItemDamageEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ItemStack item;

    private int damage;
    private boolean cancelled = false;

    public PlayerItemDamageEvent(Player player, ItemStack item, int damage) {
        super(Type.FIXED_EVENT, player);
        this.item = item;
        this.damage = damage;
    }

    /**
     * Gets the item being damaged.
     *
     * @return the item
     */
    public ItemStack getItem() {
        return this.item;
    }

    /**
     * Gets the amount of durability damage this item will be taking.
     *
     * @return durability change
     */
    public int getDamage() {
        return this.damage;
    }

    /**
     * Sets the amount of durability damage this item will be taking.
     *
     * @param damage the durability change
     */
    public void setDamage(int damage) {
        this.damage = damage;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
