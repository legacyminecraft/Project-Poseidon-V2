package org.bukkit.event.vehicle;

import org.bukkit.entity.Vehicle;
import org.bukkit.event.HandlerList;

public class VehicleUpdateEvent extends VehicleEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public VehicleUpdateEvent(Vehicle vehicle) {
        super(Type.VEHICLE_UPDATE, vehicle);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
