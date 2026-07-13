package org.bukkit.event.entity;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.Nullable;

/**
 * Called when a projectile hits a block or an entity
 */
public class ProjectileHitEvent extends EntityEvent implements Cancellable { // Poseidon - implements Cancellable

    private static final HandlerList HANDLER_LIST = new HandlerList();

    // Poseidon start - improve ProjectileHitEvent
    private final Projectile projectile;
    private final @Nullable Entity hitEntity;
    private final @Nullable Block hitBlock;
    private final @Nullable BlockFace hitFace;

    private boolean cancelled;

    public ProjectileHitEvent(Projectile projectile, Entity hitEntity) {
        super(Type.PROJECTILE_HIT, projectile);
        this.projectile = projectile;
        this.hitEntity = hitEntity;
        this.hitBlock = null;
        this.hitFace = null;
    }

    public ProjectileHitEvent(Projectile projectile, Block hitBlock, BlockFace hitFace) {
        super(Type.PROJECTILE_HIT, projectile);
        this.projectile = projectile;
        this.hitBlock = hitBlock;
        this.hitFace = hitFace;
        this.hitEntity = null;
    }

    /**
     * Gets the projectile involved in this event
     *
     * @return the projectile
     */
    public Projectile getProjectile() {
        return this.projectile;
    }

    /**
     * Gets the entity that was hit, if it was an entity that was hit
     *
     * @return hit entity or else {@code null}
     */
    public @Nullable Entity getHitEntity() {
        return this.hitEntity;
    }

    /**
     * Gets the block that was hit, if it was a block that was hit
     *
     * @return hit block or else {@code null}
     */
    public @Nullable Block getHitBlock() {
        return this.hitBlock;
    }

    /**
     * Gets the block face that was hit, if it was a block that was hit
     *
     * @return hit face or else {@code null}
     */
    public @Nullable BlockFace getHitBlockFace() {
        return this.hitFace;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
    // Poseidon end

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
