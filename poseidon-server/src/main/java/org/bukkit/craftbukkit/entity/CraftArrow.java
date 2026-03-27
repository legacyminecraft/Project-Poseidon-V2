package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityArrow;
import net.minecraft.server.EntityLiving;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

public class CraftArrow extends AbstractProjectile implements Arrow {

    public CraftArrow(CraftServer server, EntityArrow entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "CraftArrow";
    }

    public @Nullable LivingEntity getShooter() {
        if (((EntityArrow) getHandle()).shooter != null) {
            return (LivingEntity) ((EntityArrow) getHandle()).shooter.getBukkitEntity();
        }

        return null;

    }

    public void setShooter(@Nullable LivingEntity shooter) {
        if (shooter instanceof CraftLivingEntity) {
            ((EntityArrow) getHandle()).shooter = (EntityLiving) ((CraftLivingEntity) shooter).entity;
        }
    }
}
