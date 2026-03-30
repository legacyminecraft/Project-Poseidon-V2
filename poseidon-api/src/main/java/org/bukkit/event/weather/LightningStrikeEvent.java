package org.bukkit.event.weather;

import org.bukkit.World;
import org.bukkit.entity.LightningStrike;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Stores data for lightning striking
 */
public class LightningStrikeEvent extends WeatherEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private boolean canceled;
    private LightningStrike bolt;
    private World world;

    public LightningStrikeEvent(World world, LightningStrike bolt) {
        super(Type.LIGHTNING_STRIKE, world);
        this.bolt = bolt;
        this.world = world;
    }

    /**
     * Gets the bolt which is striking the earth.
     *
     * @return lightning entity
     */
    public LightningStrike getLightning() {
        return bolt;
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
