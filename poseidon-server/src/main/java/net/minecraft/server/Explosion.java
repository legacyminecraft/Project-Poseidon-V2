package net.minecraft.server;

import com.legacyminecraft.poseidon.util.BlockPos;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.Location;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class Explosion {

    public boolean a = false;
    private Random h = new Random();
    private World world;
    public double posX;
    public double posY;
    public double posZ;
    public @Nullable Entity source;
    public float size;
    public LongOpenHashSet blocks = new LongOpenHashSet(); // Poseidon - HashSet -> LongOpenHashSet

    public boolean wasCanceled = false; // CraftBukkit

    private static final double[] CACHED_RAYS; // Poseidon

    public Explosion(World world, @Nullable Entity entity, double d0, double d1, double d2, float f) {
        this.world = world;
        this.source = entity;
        this.size = f;
        this.posX = d0;
        this.posY = d1;
        this.posZ = d2;
    }

    public void a() {
        float f = this.size;

        for (int i = 0; i < CACHED_RAYS.length; i += 3) { // Poseidon - optimize explosions
            float f1 = this.size * (0.7F + this.world.random.nextFloat() * 0.6F);

            double d0 = this.posX;
            double d1 = this.posY;
            double d2 = this.posZ;

            for (float f2 = 0.3F; f1 > 0.0F; f1 -= f2 * 0.75F) {
                int l = MathHelper.floor(d0);
                int i1 = MathHelper.floor(d1);
                int j1 = MathHelper.floor(d2);
                int k1 = this.world.getTypeId(l, i1, j1);

                if (k1 > 0 && Block.byId[k1].j() != -1.0F) { // Poseidon - ignore indestructible blocks
                    f1 -= (Block.byId[k1].a(this.source) + 0.3F) * f2;
                    // Poseidon start - moved from below
                    if (f1 > 0.0F) {
                        this.blocks.add(BlockPos.of(l, i1, j1));
                    }
                    // Poseidon end
                }

                // Poseidon start - optimize explosions
                d0 += CACHED_RAYS[i] * (double) f2;
                d1 += CACHED_RAYS[i + 1] * (double) f2;
                d2 += CACHED_RAYS[i + 2] * (double) f2;
                // Poseidon end
            }
        }

        this.size *= 2.0F;
        int i = MathHelper.floor(this.posX - (double) this.size - 1.0D);
        int j = MathHelper.floor(this.posX + (double) this.size + 1.0D);
        int k = MathHelper.floor(this.posY - (double) this.size - 1.0D);
        int l1 = MathHelper.floor(this.posY + (double) this.size + 1.0D);
        int i2 = MathHelper.floor(this.posZ - (double) this.size - 1.0D);
        int j2 = MathHelper.floor(this.posZ + (double) this.size + 1.0D);
        List<Entity> list = this.world.b(this.source, AxisAlignedBB.b(i, k, i2, j, l1, j2));
        Vec3D vec3d = Vec3D.create(this.posX, this.posY, this.posZ);

        for (int k2 = 0; k2 < list.size(); ++k2) {
            Entity entity = list.get(k2);
            double d7 = entity.f(this.posX, this.posY, this.posZ) / (double) this.size;

            if (d7 <= 1.0D) {
                double d0 = entity.locX - this.posX;
                double d1 = entity.locY - this.posY;
                double d2 = entity.locZ - this.posZ;
                double d8 = MathHelper.a(d0 * d0 + d1 * d1 + d2 * d2);

                d0 /= d8;
                d1 /= d8;
                d2 /= d8;
                double d9 = getBlockDensity(vec3d, entity); // Poseidon - optimize explosions
                double d10 = (1.0D - d7) * d9;

                // CraftBukkit start - explosion damage hook
                org.bukkit.Server server = this.world.getServer();
                org.bukkit.entity.Entity damagee = (entity == null) ? null : entity.getBukkitEntity();
                int damageDone = (int) ((d10 * d10 + d10) / 2.0D * 8.0D * (double) this.size + 1.0D);

                if (damagee == null) {
                    // nothing was hurt
                } else if (this.source == null) { // Block explosion
                    EntityDamageByBlockEvent event = new EntityDamageByBlockEvent(null, damagee, EntityDamageEvent.DamageCause.BLOCK_EXPLOSION, damageDone);
                    server.getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        entity.damageEntity(this.source, event.getDamage());
                        entity.motX += d0 * d10;
                        entity.motY += d1 * d10;
                        entity.motZ += d2 * d10;
                        entity.velocityChanged = true; // Poseidon
                    }
                } else {
                    // Poseidon start - supply correct DamageCause for TNT explosions
                    org.bukkit.entity.Entity damager = this.source.getBukkitEntity();
                    EntityDamageEvent.DamageCause damageCause = damager instanceof TNTPrimed ? EntityDamageEvent.DamageCause.BLOCK_EXPLOSION : EntityDamageEvent.DamageCause.ENTITY_EXPLOSION;

                    EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(this.source.getBukkitEntity(), damagee, damageCause, damageDone);
                    // Poseidon end
                    server.getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        entity.damageEntity(this.source, event.getDamage());

                        entity.motX += d0 * d10;
                        entity.motY += d1 * d10;
                        entity.motZ += d2 * d10;
                        entity.velocityChanged = true; // Poseidon
                    }
                }
                // CraftBukkit end
            }
        }

        this.size = f;
        // Poseidon start - remove
        /*ArrayList<ChunkPosition> arraylist = new ArrayList<>();

        arraylist.addAll(this.blocks);*/
        // Poseidon end

        if (this.a) {
            // Poseidon start - ChunkPosition -> long
            this.blocks.forEach(blockPos -> {
                int i3 = BlockPos.x(blockPos);
                int j3 = BlockPos.y(blockPos);
                int k3 = BlockPos.z(blockPos);
                // Poseidon end
                int l3 = this.world.getTypeId(i3, j3, k3);
                int i4 = this.world.getTypeId(i3, j3 - 1, k3);

                if (l3 == 0 && Block.o[i4] && this.h.nextInt(3) == 0) {
                    this.world.setTypeId(i3, j3, k3, Block.FIRE.id);
                }
            });
        }
    }

    public void a(boolean flag) {
        this.world.makeSound(this.posX, this.posY, this.posZ, "random.explode", 4.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F);
        // Poseidon start - remove
        /*ArrayList<ChunkPosition> arraylist = new ArrayList<>();

        arraylist.addAll(this.blocks);*/
        // Poseidon end

        // CraftBukkit start
        org.bukkit.World bworld = this.world.getWorld();
        org.bukkit.entity.Entity explode = this.source == null ? null : this.source.getBukkitEntity();
        Location location = new Location(bworld, this.posX, this.posY, this.posZ);

        // Poseidon start - optimize explosions
        List<org.bukkit.block.Block> blockList = new ObjectArrayList<>();
        this.blocks.forEach(blockPos -> {
            int x = BlockPos.x(blockPos);
            int y = BlockPos.y(blockPos);
            int z = BlockPos.z(blockPos);
            org.bukkit.block.Block block = bworld.getBlockAt(x, y, z);
            if (block.getType() != org.bukkit.Material.AIR) {
                blockList.add(block);
            }
        });
        // Poseidon end

        EntityExplodeEvent event = new EntityExplodeEvent(explode, location, blockList);
        this.world.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            this.wasCanceled = true;
            return;
        }
        // CraftBukkit end

        // Poseidon start - fix EntityExplodeEvent
        this.blocks.clear();
        event.blockList().forEach(block -> {
            if (block.getWorld() == bworld && block.getType() != org.bukkit.Material.AIR) {
                this.blocks.add(BlockPos.of(block.getX(), block.getY(), block.getZ()));
            }
        });
        // Poseidon end

        // Poseidon start - ChunkPosition -> long
        this.blocks.forEach(blockPos -> {
            int j = BlockPos.x(blockPos);
            int k = BlockPos.y(blockPos);
            int l = BlockPos.z(blockPos);
            // Poseidon end
            int i1 = this.world.getTypeId(j, k, l);

            if (flag) {
                double d0 = (float) j + this.world.random.nextFloat();
                double d1 = (float) k + this.world.random.nextFloat();
                double d2 = (float) l + this.world.random.nextFloat();
                double d3 = d0 - this.posX;
                double d4 = d1 - this.posY;
                double d5 = d2 - this.posZ;
                double d6 = MathHelper.a(d3 * d3 + d4 * d4 + d5 * d5);

                d3 /= d6;
                d4 /= d6;
                d5 /= d6;
                double d7 = 0.5D / (d6 / (double) this.size + 0.1D);

                d7 *= this.world.random.nextFloat() * this.world.random.nextFloat() + 0.3F;
                d3 *= d7;
                d4 *= d7;
                d5 *= d7;
                this.world.a("explode", (d0 + this.posX) / 2.0D, (d1 + this.posY) / 2.0D, (d2 + this.posZ) / 2.0D, d3, d4, d5);
                this.world.a("smoke", d0, d1, d2, d3, d4, d5);
            }

            // CraftBukkit - stop explosions from putting out fire
            if (i1 > 0 && i1 != Block.FIRE.id) {
                // CraftBukkit
                Block.byId[i1].dropNaturally(this.world, j, k, l, this.world.getData(j, k, l), event.getYield());
                this.world.setTypeId(j, k, l, 0);
                Block.byId[i1].d(this.world, j, k, l);
            }
        });
    }

    // Poseidon start - optimize explosions
    private float getBlockDensity(Vec3D vec3d, Entity entity) {
        int key = getCacheKey(entity.boundingBox);
        return (float) this.world.explosionDensityCache.computeIfAbsent(key, _ -> this.world.a(vec3d, entity.boundingBox));
    }

    private int getCacheKey(AxisAlignedBB aabb) {
        int result;
        long temp;
        result = this.world.hashCode();
        temp = Double.doubleToLongBits(this.posX);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.posY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.posZ);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(aabb.a);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(aabb.b);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(aabb.c);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(aabb.d);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(aabb.e);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(aabb.f);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    static {
        DoubleArrayList rayCoords = new DoubleArrayList();

        byte b0 = 16;
        for (int i = 0; i < b0; ++i) {
            for (int j = 0; j < b0; ++j) {
                for (int k = 0; k < b0; ++k) {
                    if (i == 0 || i == b0 - 1 || j == 0 || j == b0 - 1 || k == 0 || k == b0 - 1) {
                        double d3 = ((float) i / ((float) b0 - 1.0F) * 2.0F - 1.0F);
                        double d4 = ((float) j / ((float) b0 - 1.0F) * 2.0F - 1.0F);
                        double d5 = ((float) k / ((float) b0 - 1.0F) * 2.0F - 1.0F);
                        double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

                        rayCoords.add(d3 / d6);
                        rayCoords.add(d4 / d6);
                        rayCoords.add(d5 / d6);
                    }
                }
            }
        }

        CACHED_RAYS = rayCoords.toDoubleArray();
    }
    // Poseidon end
}
