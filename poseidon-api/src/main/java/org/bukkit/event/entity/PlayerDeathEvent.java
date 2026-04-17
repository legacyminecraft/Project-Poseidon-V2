package org.bukkit.event.entity;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Called whenever a {@link Player} dies.
 */
public class PlayerDeathEvent extends EntityDeathEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final PlayerInventory inventoryToKeep;

    private @Nullable String deathMessage;

    public PlayerDeathEvent(Player player, List<ItemStack> drops, PlayerInventory inventoryToKeep) {
        super(player, drops);
        this.inventoryToKeep = inventoryToKeep;
    }

    @Override
    public Player getEntity() {
        return (Player) this.entity;
    }

    /**
     * Returns the player involved in this event
     *
     * @return Player who is involved in this event
     */
    public Player getPlayer() {
        return getEntity();
    }

    /**
     * Get the death message that will appear to everyone on the server.
     *
     * @return Message to appear to other players on the server.
     */
    public @Nullable String getDeathMessage() {
        return this.deathMessage;
    }

    /**
     * Set the death message that will appear to everyone on the server.
     *
     * @param deathMessage message to appear to other players on the server.
     */
    public void setDeathMessage(@Nullable String deathMessage) {
        this.deathMessage = deathMessage;
    }

    /**
     * A player inventory to add items that the player should retain in their
     * inventory on death (similar to keep inventory).
     * <p>
     * You <b>MUST</b> remove an item from the {@link PlayerDeathEvent#getDrops()}
     * collection too or it will duplicate!
     */
    public PlayerInventory getInventoryToKeep() {
        return this.inventoryToKeep;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
