package org.bukkit.entity;

import org.bukkit.util.Vector;

/**
 * Represents a minecart entity.
 *
 * @author sk89q
 */
public interface Minecart extends Vehicle {

    /**
     * Sets a minecart's damage.
     *
     * @param damage over 40 to "kill" a minecart
     */
    void setDamage(int damage);

    /**
     * Gets a minecart's damage.
     *
     * @param damage
     */
    int getDamage();

    /**
     * Gets the maximum speed of a minecart. The speed is unrelated to the velocity.
     *
     * @param speed
     */
    double getMaxSpeed();

    /**
     * Sets the maximum speed of a minecart. Must be nonnegative. Default is 0.4D.
     *
     * @param speed
     */
    void setMaxSpeed(double speed);

    /**
     * Returns whether this minecart will slow down faster without a passenger occupying it
     *
     */
    boolean isSlowWhenEmpty();

    /**
     * Sets whether this minecart will slow down faster without a passenger occupying it
     *
     * @param slow
     */
    void setSlowWhenEmpty(boolean slow);

    /**
     * Gets the flying velocity modifier. Used for minecarts that are in mid-air.
     * A flying minecart's velocity is multiplied by this factor each tick.
     *
     * @param flying velocity modifier
     */
    Vector getFlyingVelocityMod();

    /**
     * Sets the flying velocity modifier. Used for minecarts that are in mid-air.
     * A flying minecart's velocity is multiplied by this factor each tick.
     *
     * @param flying velocity modifier
     */
    void setFlyingVelocityMod(Vector flying);

    /**
     * Gets the derailed velocity modifier. Used for minecarts that are on the ground, but not on rails.
     *
     * A derailed minecart's velocity is multiplied by this factor each tick.
     * @param visible speed
     */
    Vector getDerailedVelocityMod();

    /**
     * Sets the derailed velocity modifier. Used for minecarts that are on the ground, but not on rails.
     * A derailed minecart's velocity is multiplied by this factor each tick.
     *
     * @param visible speed
     */
    void setDerailedVelocityMod(Vector derailed);
}
