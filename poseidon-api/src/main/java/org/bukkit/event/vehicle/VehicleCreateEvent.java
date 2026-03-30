package org.bukkit.event.vehicle;

import org.bukkit.entity.Vehicle;
import org.bukkit.event.HandlerList;

/**
 * Raised when a vehicle is created.
 *
 * @author sk89q
 */
public class VehicleCreateEvent extends VehicleEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public VehicleCreateEvent(Vehicle vehicle) {
        super(Type.VEHICLE_CREATE, vehicle);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
