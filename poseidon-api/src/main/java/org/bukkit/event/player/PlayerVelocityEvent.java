package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

/**
 * Holds information for player velocity events
 */
public class PlayerVelocityEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private boolean cancel = false;
    private Vector velocity;

    public PlayerVelocityEvent(final Player player, final Vector velocity) {
        super(Type.PLAYER_VELOCITY, player);
        this.velocity = velocity;
    }

    PlayerVelocityEvent(final Event.Type type, final Player player, final Vector velocity) {
        super(type, player);
        this.velocity = velocity;
    }

    /**
     * Gets the velocity vector that will be sent to the player
     *
     * @return Vector the player will get
     */
    public Vector getVelocity() {
        return velocity;
    }

    /**
     * Sets the velocity vector that will be sent to the player
     *
     * @param velocity The velocity vector that will be sent to the player
     */
    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
