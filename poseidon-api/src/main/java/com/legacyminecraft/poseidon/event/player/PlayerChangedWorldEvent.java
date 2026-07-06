package com.legacyminecraft.poseidon.event.player;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called when a player switches to another world.
 */
public class PlayerChangedWorldEvent extends PlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final World from;

    public PlayerChangedWorldEvent(Player player, World from) {
        super(Type.FIXED_EVENT, player);
        this.from = from;
    }

    /**
     * Gets the world the player is switching from.
     *
     * @return player's previous world
     */
    public World getFrom() {
        return this.from;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
