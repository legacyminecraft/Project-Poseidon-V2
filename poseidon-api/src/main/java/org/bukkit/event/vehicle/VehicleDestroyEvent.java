package org.bukkit.event.vehicle;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.Nullable;

/**
 * Raised when a vehicle is destroyed
 */
public class VehicleDestroyEvent extends VehicleEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private @Nullable Entity attacker;
    private boolean cancelled;

    public VehicleDestroyEvent(Vehicle vehicle, @Nullable Entity attacker) {
        super(Type.VEHICLE_DESTROY, vehicle);
        this.attacker = attacker;
    }

    /**
     * Gets the Entity that has destroyed the vehicle
     *
     * @return the Entity that has destroyed the vehicle
     */
    public @Nullable Entity getAttacker() {
        return attacker;
    }

    public boolean isCancelled() {
        return cancelled;
    }

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
