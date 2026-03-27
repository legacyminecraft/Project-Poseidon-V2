package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityFish;
import net.minecraft.server.EntityHuman;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Fish;
import org.bukkit.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

public class CraftFish extends AbstractProjectile implements Fish {
    public CraftFish(CraftServer server, EntityFish entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "CraftFish";
    }

    public @Nullable LivingEntity getShooter() {
        if (((EntityFish) getHandle()).owner != null) {
            return (LivingEntity) ((EntityFish) getHandle()).owner.getBukkitEntity();
        }

        return null;

    }

    public void setShooter(@Nullable LivingEntity shooter) {
        if (shooter instanceof CraftHumanEntity) {
            ((EntityFish) getHandle()).owner = (EntityHuman) ((CraftHumanEntity) shooter).entity;
        }
    }

}
