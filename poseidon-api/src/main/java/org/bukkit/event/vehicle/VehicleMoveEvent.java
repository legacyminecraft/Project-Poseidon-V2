package org.bukkit.event.vehicle;

import org.bukkit.Location;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.HandlerList;

/**
 * Raised when a vehicle moves.
 *
 * @author sk89q
 */
public class VehicleMoveEvent extends VehicleEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private Location from;
    private Location to;

    public VehicleMoveEvent(Vehicle vehicle, Location from, Location to) {
        super(Type.VEHICLE_MOVE, vehicle);

        this.from = from;
        this.to = to;
    }

    /**
     * Get the previous position.
     *
     * @return
     */
    public Location getFrom() {
        return from;
    }

    /**
     * Get the next position.
     *
     * @return
     */
    public Location getTo() {
        return to;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
