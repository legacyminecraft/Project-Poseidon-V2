package org.bukkit.event.weather;

import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Stores data for weather changing in a world
 */
public class WeatherChangeEvent extends WeatherEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private boolean canceled;
    private boolean to;

    public WeatherChangeEvent(World world, boolean to) {
        super(Type.WEATHER_CHANGE, world);
        this.to = to;
    }

    /**
     * Gets the state of weather that the world is being set to
     *
     * @return true if the weather is being set to raining, false otherwise
     */
    public boolean toWeatherState() {
        return to;
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
