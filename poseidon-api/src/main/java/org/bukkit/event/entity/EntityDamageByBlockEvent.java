package org.bukkit.event.entity;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.jspecify.annotations.Nullable;

/**
 * Called when an entity is damaged by a block
 */
public class EntityDamageByBlockEvent extends EntityDamageEvent implements Cancellable {

    private @Nullable Block damager;

    public EntityDamageByBlockEvent(@Nullable Block damager, Entity damagee, DamageCause cause, int damage) {
        super(Type.ENTITY_DAMAGE, damagee, cause, damage);
        this.damager = damager;
    }

    /**
     * Returns the block that damaged the player.
     *
     * @return Block that damaged the player
     */
    public @Nullable Block getDamager() {
        return damager;
    }
}
