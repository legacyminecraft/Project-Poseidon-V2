package net.minecraft.server;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class EntityTrackerEntry {

    public Entity tracker;
    public int b;
    public int c;
    public int d;
    public int e;
    public int f;
    public int g;
    public int h;
    public double i;
    public double j;
    public double k;
    public int l = 0;
    private double o;
    private double p;
    private double q;
    private boolean r = false;
    private boolean isMoving;
    private int t = 0;
    public boolean m = false;
    public ObjectOpenHashSet<EntityPlayer> trackedPlayers = new ObjectOpenHashSet<>(); // Poseidon - HashSet -> ObjectOpenHashSet

    public EntityTrackerEntry(Entity entity, int i, int j, boolean flag) {
        this.tracker = entity;
        this.b = i;
        this.c = j;
        this.isMoving = flag;
        this.d = MathHelper.floor(entity.locX * 32.0D);
        this.e = MathHelper.floor(entity.locY * 32.0D);
        this.f = MathHelper.floor(entity.locZ * 32.0D);
        this.g = MathHelper.d(entity.yaw * 256.0F / 360.0F);
        this.h = MathHelper.d(entity.pitch * 256.0F / 360.0F);
    }

    public boolean equals(Object object) {
        return object instanceof EntityTrackerEntry entitytrackerentry && entitytrackerentry.tracker.id == this.tracker.id;
    }

    public int hashCode() {
        return this.tracker.id;
    }

    public void track(List<EntityHuman> list) {
        this.m = false;
        if (!this.r || this.tracker.e(this.o, this.p, this.q) > 16.0D) {
            this.o = this.tracker.locX;
            this.p = this.tracker.locY;
            this.q = this.tracker.locZ;
            this.r = true;
            this.m = true;
            this.scanPlayers(list);
        }

        if (this.l % this.c == 0 || this.tracker.airBorne || this.tracker.aa().a()) { // Poseidon
            ++this.t; // Poseidon - moved from above
            int i = MathHelper.floor(this.tracker.locX * 32.0D);
            int j = MathHelper.floor(this.tracker.locY * 32.0D);
            int k = MathHelper.floor(this.tracker.locZ * 32.0D);
            int l = MathHelper.d(this.tracker.yaw * 256.0F / 360.0F);
            int i1 = MathHelper.d(this.tracker.pitch * 256.0F / 360.0F);
            int j1 = i - this.d;
            int k1 = j - this.e;
            int l1 = k - this.f;
            Packet object = null;

            // Poseidon start - lower update threshold
            boolean flag = Math.abs(j1) >= 4 || Math.abs(k1) >= 4 || Math.abs(l1) >= 4 || this.l % 60 == 0;
            boolean flag1 = Math.abs(l - this.g) >= 4 || Math.abs(i1 - this.h) >= 4;
            // Poseidon end

            // Poseidon start - code moved from below
            if (flag) {
                this.d = i;
                this.e = j;
                this.f = k;
            }

            if (flag1) {
                this.g = l;
                this.h = i1;
            }
            // Poseidon end

            if (j1 >= -128 && j1 < 128 && k1 >= -128 && k1 < 128 && l1 >= -128 && l1 < 128 && this.t <= 400) {
                if (flag && flag1) {
                    object = new Packet33RelEntityMoveLook(this.tracker.id, (byte) j1, (byte) k1, (byte) l1, (byte) l, (byte) i1);
                } else if (flag) {
                    object = new Packet31RelEntityMove(this.tracker.id, (byte) j1, (byte) k1, (byte) l1);
                } else if (flag1) {
                    object = new Packet32EntityLook(this.tracker.id, (byte) l, (byte) i1);
                }
            } else {
                this.t = 0;
                // Poseidon start
                /*this.tracker.locX = (double) i / 32.0D;
                this.tracker.locY = (double) j / 32.0D;
                this.tracker.locZ = (double) k / 32.0D;*/
                if (this.tracker instanceof EntityPlayer) {
                    this.scanPlayers(new ObjectArrayList<>(this.trackedPlayers));
                }
                // Poseidon end
                object = new Packet34EntityTeleport(this.tracker.id, i, j, k, (byte) l, (byte) i1);
            }

            if (this.isMoving) {
                double d0 = this.tracker.motX - this.i;
                double d1 = this.tracker.motY - this.j;
                double d2 = this.tracker.motZ - this.k;
                double d3 = 0.02D;
                double d4 = d0 * d0 + d1 * d1 + d2 * d2;

                if (d4 > d3 * d3 || d4 > 0.0D && this.tracker.motX == 0.0D && this.tracker.motY == 0.0D && this.tracker.motZ == 0.0D) {
                    this.i = this.tracker.motX;
                    this.j = this.tracker.motY;
                    this.k = this.tracker.motZ;
                    this.a(new Packet28EntityVelocity(this.tracker.id, this.i, this.j, this.k));
                }
            }

            if (object != null) {
                this.a(object);
            }

            DataWatcher datawatcher = this.tracker.aa();

            if (datawatcher.a()) {
                this.b(new Packet40EntityMetadata(this.tracker.id, datawatcher));
            }

            // Poseidon start - code moved up
            /*if (flag) {
                this.d = i;
                this.e = j;
                this.f = k;
            }

            if (flag1) {
                this.g = l;
                this.h = i1;
            }*/
            this.tracker.airBorne = false;
            // Poseidon end
        }

        ++this.l; // Poseidon
        if (this.tracker.velocityChanged) {
            // CraftBukkit start - create PlayerVelocity event
            boolean cancelled = false;

            if(this.tracker instanceof EntityPlayer) {
                org.bukkit.entity.Player player = (org.bukkit.entity.Player) this.tracker.getBukkitEntity();
                org.bukkit.util.Vector velocity = player.getVelocity();

                org.bukkit.event.player.PlayerVelocityEvent event = new org.bukkit.event.player.PlayerVelocityEvent(player, velocity);
                this.tracker.world.getServer().getPluginManager().callEvent(event);

                if(event.isCancelled()) {
                    cancelled = true;
                }
                else if(!velocity.equals(event.getVelocity())) {
                    player.setVelocity(velocity);
                }
            }

            if(!cancelled) {
                this.b(new Packet28EntityVelocity(this.tracker));
            }
            // CraftBukkit end
            this.tracker.velocityChanged = false;
        }
    }

    public void a(Packet packet) {
        this.trackedPlayers.forEach(entityplayer -> entityplayer.netServerHandler.sendPacket(packet)); // Poseidon - forEach
    }

    public void b(Packet packet) {
        this.a(packet);
        if (this.tracker instanceof EntityPlayer entityplayer) {
            entityplayer.netServerHandler.sendPacket(packet);
        }
    }

    public void a() {
        this.trackedPlayers.forEach(entityplayer -> entityplayer.removeQueue.add(this.tracker.id)); // Poseidon
    }

    public void a(EntityPlayer entityplayer) {
        // Poseidon start
        if (this.trackedPlayers.remove(entityplayer)) {
            entityplayer.removeQueue.add(this.tracker.id);
        }
        // Poseidon end
    }

    public void b(EntityPlayer entityplayer) {
        if (entityplayer != this.tracker) {
            // Poseidon start - use location of tracked entity
            double d0 = entityplayer.locX - this.tracker.locX;
            double d1 = entityplayer.locZ - this.tracker.locZ;
            // Poseidon end

            if (d0 >= (double) (-this.b) && d0 <= (double) this.b && d1 >= (double) (-this.b) && d1 <= (double) this.b) {
                if (!this.trackedPlayers.contains(entityplayer) && this.d(entityplayer)) { // Poseidon
                    entityplayer.removeQueue.rem(this.tracker.id); // Poseidon
                    this.trackedPlayers.add(entityplayer);
                    entityplayer.netServerHandler.sendPacket(this.b());

                    // Poseidon start
                    if (!this.tracker.datawatcher.d) {
                        entityplayer.netServerHandler.sendPacket(new Packet40EntityMetadata(this.tracker.id, this.tracker.datawatcher));
                    }

                    this.i = this.tracker.motX;
                    this.j = this.tracker.motY;
                    this.k = this.tracker.motZ;
                    if (this.isMoving) {
                        entityplayer.netServerHandler.sendPacket(new Packet28EntityVelocity(this.tracker.id, this.tracker.motX, this.tracker.motY, this.tracker.motZ));
                    }

                    if (this.tracker.vehicle != null) {
                        entityplayer.netServerHandler.sendPacket(new Packet39AttachEntity(this.tracker, this.tracker.vehicle));
                    }

                    if (this.tracker.passenger != null) {
                        entityplayer.netServerHandler.sendPacket(new Packet39AttachEntity(this.tracker.passenger, this.tracker));
                    }
                    // Poseidon end

                    @Nullable ItemStack[] aitemstack = this.tracker.getEquipment();

                    if (aitemstack != null) {
                        for (int i = 0; i < aitemstack.length; ++i) {
                            entityplayer.netServerHandler.sendPacket(new Packet5EntityEquipment(this.tracker.id, i, aitemstack[i]));
                        }
                    }

                    if (this.tracker instanceof EntityHuman entityhuman) {
                        if (entityhuman.isSleeping()) {
                            entityplayer.netServerHandler.sendPacket(new Packet17(this.tracker, 0, MathHelper.floor(this.tracker.locX), MathHelper.floor(this.tracker.locY), MathHelper.floor(this.tracker.locZ)));
                        }
                    }
                }
            } else if (this.trackedPlayers.remove(entityplayer)) { // Poseidon - remove redundant contains check
                entityplayer.removeQueue.add(this.tracker.id); // Poseidon
            }
        }
    }

    // Poseidon start
    private boolean d(EntityPlayer entityplayer) {
        return entityplayer.getWorldServer().getPlayerManager().a(entityplayer, this.tracker.bH, this.tracker.bJ);
    }
    // Poseidon end

    public void scanPlayers(List<EntityHuman> list) {
        for (int i = 0; i < list.size(); ++i) {
            EntityHuman entityhuman = list.get(i);
            if (entityhuman instanceof EntityPlayer entityplayer) {
                this.b(entityplayer);
            }
        }
    }

    private @Nullable Packet b() {
        // Poseidon start
        if (this.tracker.dead) {
            return null;
        }
        // Poseidon end

        if (this.tracker instanceof EntityItem entityitem) {
            Packet21PickupSpawn packet21pickupspawn = new Packet21PickupSpawn(entityitem);

            // Poseidon start - remove
            /*entityitem.locX = (double) packet21pickupspawn.b / 32.0D;
            entityitem.locY = (double) packet21pickupspawn.c / 32.0D;
            entityitem.locZ = (double) packet21pickupspawn.d / 32.0D;*/
            // Poseidon end
            return packet21pickupspawn;
        } else if (this.tracker instanceof EntityPlayer entityplayer) {
            // CraftBukkit start - limit name length to 16 characters
            if (entityplayer.name.length() > 16) {
                entityplayer.name = entityplayer.name.substring(0, 16);
            }
            // CraftBukkit end
            return new Packet20NamedEntitySpawn(entityplayer);
        } else {
            if (this.tracker instanceof EntityMinecart entityminecart) {
                if (entityminecart.type == 0) {
                    return new Packet23VehicleSpawn(this.tracker, 10);
                }

                if (entityminecart.type == 1) {
                    return new Packet23VehicleSpawn(this.tracker, 11);
                }

                if (entityminecart.type == 2) {
                    return new Packet23VehicleSpawn(this.tracker, 12);
                }
            }

            if (this.tracker instanceof EntityBoat) {
                return new Packet23VehicleSpawn(this.tracker, 1);
            } else if (this.tracker instanceof EntityLiving entityliving) { // Poseidon - IAnimal -> EntityLiving
                return new Packet24MobSpawn(entityliving);
            } else if (this.tracker instanceof EntityFish) {
                return new Packet23VehicleSpawn(this.tracker, 90);
            } else if (this.tracker instanceof EntityArrow entityarrow) {
                EntityLiving entityliving = entityarrow.shooter;

                return new Packet23VehicleSpawn(this.tracker, 60, entityliving != null ? entityliving.id : this.tracker.id);
            } else if (this.tracker instanceof EntitySnowball) {
                return new Packet23VehicleSpawn(this.tracker, 61);
            } else if (this.tracker instanceof EntityFireball entityfireball) {
                // CraftBukkit start - added check for null shooter
                int shooter = entityfireball.shooter != null ? entityfireball.shooter.id : 1;
                Packet23VehicleSpawn packet23vehiclespawn = new Packet23VehicleSpawn(this.tracker, 63, shooter);
                // CraftBukkit end

                packet23vehiclespawn.e = (int) (entityfireball.c * 8000.0D);
                packet23vehiclespawn.f = (int) (entityfireball.d * 8000.0D);
                packet23vehiclespawn.g = (int) (entityfireball.e * 8000.0D);
                return packet23vehiclespawn;
            } else if (this.tracker instanceof EntityEgg) {
                return new Packet23VehicleSpawn(this.tracker, 62);
            } else if (this.tracker instanceof EntityTNTPrimed) {
                return new Packet23VehicleSpawn(this.tracker, 50);
            } else {
                if (this.tracker instanceof EntityFallingSand entityfallingsand) {
                    if (entityfallingsand.a == Block.SAND.id) {
                        return new Packet23VehicleSpawn(this.tracker, 70);
                    }

                    if (entityfallingsand.a == Block.GRAVEL.id) {
                        return new Packet23VehicleSpawn(this.tracker, 71);
                    }
                }

                if (this.tracker instanceof EntityPainting entitypainting) {
                    return new Packet25EntityPainting(entitypainting);
                } else {
                    throw new IllegalArgumentException("Don't know how to add " + this.tracker.getClass() + "!");
                }
            }
        }
    }

    public void c(EntityPlayer entityplayer) {
        if (this.trackedPlayers.remove(entityplayer)) { // Poseidon - remove redundant contains check
            entityplayer.removeQueue.add(this.tracker.id); // Poseidon
        }
    }
}
