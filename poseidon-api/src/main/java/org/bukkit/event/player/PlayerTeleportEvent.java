package org.bukkit.event.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Holds information for player teleport events
 */
public class PlayerTeleportEvent extends PlayerMoveEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public PlayerTeleportEvent(Player player, Location from, Location to) {
        super(Type.PLAYER_TELEPORT, player, from, to);
    }

    public PlayerTeleportEvent(final Event.Type type, Player player, Location from, Location to) {
        super(type, player, from, to);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
