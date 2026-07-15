package com.legacyminecraft.poseidon.world;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.Chunk;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EnumCreatureType;
import net.minecraft.server.MathHelper;
import net.minecraft.server.World;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public final class LocalMobCapCalculator {

    private final Object2ObjectMap<EntityPlayer, ObjectArrayList<Chunk>> chunksNearPlayer = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectMap<EntityPlayer, MobCounts> playerMobCounts = new Object2ObjectOpenHashMap<>();

    public void prepare(World world) {
        this.chunksNearPlayer.clear();
        this.playerMobCounts.clear();

        for (int i = 0; i < world.players.size(); i++) {
            EntityHuman entityhuman = world.players.get(i);
            if (!(entityhuman instanceof EntityPlayer entityplayer) || entityhuman.dead) {
                continue;
            }

            int x = MathHelper.floor(entityplayer.locX) >> 4;
            int z = MathHelper.floor(entityplayer.locZ) >> 4;
            int range = Math.min(world.getConfig().entities.mobSpawningRange, world.getServer().getViewDistance());
            for (int dx = -range; dx <= range; dx++) {
                for (int dz = -range; dz <= range; dz++) {
                    Chunk chunk = world.getChunkAt(x + dx, z + dz);
                    addChunkToPlayer(entityplayer, chunk);

                    for (int j = 0; j < chunk.entitySlices.length; j++) {
                        List<Entity> slice = chunk.entitySlices[j];
                        for (int k = 0; k < slice.size(); k++) {
                            Entity entity = slice.get(k);
                            EnumCreatureType creatureType = getCreatureType(entity);
                            if (creatureType != null) {
                                addMobToNearbyPlayer(entityplayer, creatureType);
                            }
                        }
                    }
                }
            }
        }
    }

    private static @Nullable EnumCreatureType getCreatureType(Entity entity) {
        for (EnumCreatureType creatureType : EnumCreatureType.values()) {
            if (creatureType.a().isAssignableFrom(entity.getClass())) {
                return creatureType;
            }
        }
        return null;
    }

    public void forEachEntry(BiConsumer<EntityPlayer, ObjectArrayList<Chunk>> consumer) {
        this.chunksNearPlayer.forEach(consumer);
    }

    public void addChunkToPlayer(EntityPlayer player, Chunk chunk) {
        this.chunksNearPlayer.computeIfAbsent(player, _ -> new ObjectArrayList<>()).add(chunk);
    }

    public void addMobToNearbyPlayer(EntityPlayer player, EnumCreatureType creatureType) {
        this.playerMobCounts.computeIfAbsent(player, _ -> new MobCounts()).add(creatureType);
    }

    public boolean canSpawnForPlayer(EnumCreatureType creatureType, EntityPlayer player) {
        MobCounts mobCounts = this.playerMobCounts.get(player);
        return mobCounts == null || mobCounts.canSpawn(creatureType, player.world);
    }

    private static final class MobCounts {
        private final Map<EnumCreatureType, Integer> counts = new EnumMap<>(EnumCreatureType.class);

        private void add(EnumCreatureType creatureType) {
            this.counts.compute(creatureType, (_, v) -> v == null ? 1 : v + 1);
        }

        private boolean canSpawn(EnumCreatureType creatureType, World world) {
            return this.counts.getOrDefault(creatureType, 0) <= creatureType.getMobCap(world);
        }
    }
}
