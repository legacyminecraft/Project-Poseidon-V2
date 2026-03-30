package org.bukkit.event.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.Nullable;

/**
 * Thrown when a player is fishing
 */
public class PlayerFishEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final @Nullable Entity entity;
    private boolean cancel = false;
    private State state;

    public PlayerFishEvent(final Player player, final @Nullable Entity entity, State state) {
        super(Type.PLAYER_FISH, player);
        this.entity = entity;
        this.state = state;
    }

    /**
     * Gets the entity caught by the player
     *
     * @return Entity caught by the player, null if fishing, bobber has gotten stuck in the ground or nothing has been caught
     */
    public @Nullable Entity getCaught() {
        return entity;
    }

    /**
     * Gets the state of the fishing
     *
     * @return A State detailing the state of the fishing
     */
    public State getState() {
        return state;
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

    /**
     * An enum to specify the state of the fishing
     */
    public enum State {

        /**
         * When a player is fishing
         */
        FISHING,
        /**
         * When a player has successfully caught a fish
         */
        CAUGHT_FISH,
        /**
         * When a player has successfully caught an entity
         */
        CAUGHT_ENTITY,
        /**
         * When a bobber is stuck in the grund
         */
        IN_GROUND,
        /**
         * When a player fails to catch anything while fishing usually due to poor aiming or timing
         */
        FAILED_ATTEMPT,
    }
}
