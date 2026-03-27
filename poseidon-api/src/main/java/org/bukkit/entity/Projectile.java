package org.bukkit.entity;

import org.jspecify.annotations.Nullable;

/**
 * Represents a shootable entity
 */
public interface Projectile extends Entity {

    /**
     * Retrieve the shooter of this projectile. The returned value can be null
     * for projectiles shot from a {@link Dispenser} for example.
     *
     * @return the {@link LivingEntity} that shot this projectile
     */
    @Nullable LivingEntity getShooter();

    /**
     * Set the shooter of this projectile
     *
     * @param shooter the {@link LivingEntity} that shot this projectile
     */
    void setShooter(@Nullable LivingEntity shooter);

    /**
     * Determine if this projectile should bounce or not when it hits.
     *
     * @return true if it should bounce.
     */
    boolean doesBounce();

    /**
     * Set whether or not this projectile should bounce or not when it hits something.
     *
     * @param doesBounce whether or not it should bounce.
     */
    void setBounce(boolean doesBounce);
}
