package org.bukkit.event.entity;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Stores data for pigs being zapped
 */
public class PigZapEvent extends EntityEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private boolean canceled;
    private Entity pig;
    private Entity pigzombie;
    private Entity bolt;

    public PigZapEvent(Entity pig, Entity bolt, Entity pigzombie) {
        super(Type.PIG_ZAP, pig);
        this.pig = pig;
        this.bolt = bolt;
        this.pigzombie = pigzombie;
    }

    /**
     * Gets the bolt which is striking the pig.
     *
     * @return lightning entity
     */
    public Entity getLightning() {
        return bolt;
    }

    /**
     * Gets the zombie pig that will replace the pig,
     * provided the event is not cancelled first.
     *
     * @return resulting entity
     */
    public Entity getPigZombie() {
        return pigzombie;
    }

    public boolean isCancelled() {
        return canceled;
    }

    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
