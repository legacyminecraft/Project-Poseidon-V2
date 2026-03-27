package org.bukkit.entity;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Represents a base entity in the world
 */
public interface Entity {

    /**
     * Gets the entity's current position
     *
     * @return Location containing the position of this entity
     */
    Location getLocation();

    /**
     * Sets this entity's velocity
     *
     * @param velocity New velocity to travel with
     */
    void setVelocity(Vector velocity);

    /**
     * Gets this entity's current velocity
     *
     * @return Current travelling velocity of this entity
     */
    Vector getVelocity();

    /**
     * Gets the current world this entity resides in
     *
     * @return World
     */
    World getWorld();

    /**
     * Teleports this entity to the given location
     *
     * @param location New location to teleport this entity to
     * @return <code>true</code> if the teleport was successful
     */
    boolean teleport(Location location);

    /**
     * Teleports this entity to the target Entity
     *
     * @param destination Entity to teleport this entity to
     * @return <code>true</code> if the teleport was successful
     */
    boolean teleport(Entity destination);

    /**
     * Returns a list of entities within a bounding box defined by x,y,z centered around player
     *
     * @param x Size of the box along x axis
     * @param y Size of the box along y axis
     * @param z Size of the box along z axis
     * @return List<Entity> List of entities nearby
     */
    List<org.bukkit.entity.Entity> getNearbyEntities(double x, double y, double z);

    /**
     * Returns a unique id for this entity
     *
     * @return Entity id
     */
    int getEntityId();

    /**
     * Returns the entity's current fire ticks (ticks before the entity stops being on fire).
     *
     * @return int fireTicks
     */
    int getFireTicks();

    /**
     * Returns the entity's maximum fire ticks.
     *
     * @return int maxFireTicks
     */
    int getMaxFireTicks();

    /**
     * Sets the entity's current fire ticks (ticks before the entity stops being on fire).
     *
     * @param ticks
     */
    void setFireTicks(int ticks);

    /**
     * Mark the entity's removal.
     */
    void remove();

    /**
     * Returns true if this entity has been marked for removal.
     */
    boolean isDead();

    /**
     * Gets the {@link Server} that contains this Entity
     *
     * @return Server instance running this Entity
     */
    Server getServer();

    /**
     * Gets the primary passenger of a vehicle. For vehicles that could have
     * multiple passengers, this will only return the primary passenger.
     *
     * @return an entity
     */
    @Nullable Entity getPassenger();

    /**
     * Set the passenger of a vehicle.
     *
     * @param passenger
     * @return false if it could not be done for whatever reason
     */
    boolean setPassenger(Entity passenger);

    /**
     * Returns true if the vehicle has no passengers.
     *
     * @return
     */
    boolean isEmpty();

    /**
     * Eject any passenger. True if there was a passenger.
     *
     * @return
     */
    boolean eject();

    /**
     * Returns the distance this entity has fallen
     * @return
     */
    float getFallDistance();

    /**
     * Sets the fall distance for this entity
     * @param distance
     */
    void setFallDistance(float distance);

    /**
     * Record the last {@link EntityDamageEvent} inflicted on this entity
     * @param event a {@link EntityDamageEvent}
     */
    void setLastDamageCause(EntityDamageEvent event);

    /**
     * Retrieve the last {@link EntityDamageEvent} inflicted on this entity. This event may have been cancelled.
     * @return the last known {@link EntityDamageEvent} or null if hitherto unharmed
     */
    @Nullable EntityDamageEvent getLastDamageCause();

    /**
     * Returns a unique and persistent id for this entity
     * @return unique id
     */
    UUID getUniqueId();
}
