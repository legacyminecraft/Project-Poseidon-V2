package net.minecraft.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class EntityTracker {

    private Set<EntityTrackerEntry> a = new HashSet<>();
    private EntityList b = new EntityList();
    private MinecraftServer c;
    private int d;
    private int e;

    public EntityTracker(MinecraftServer minecraftserver, int i) {
        this.c = minecraftserver;
        this.e = i;
        this.d = minecraftserver.serverConfigurationManager.a();
    }

    // CraftBukkit - synchronized
    public synchronized void track(Entity entity) {
        switch (entity) {
            case EntityPlayer entityplayer -> {
                this.a(entity, 512, 2);
                Iterator<EntityTrackerEntry> iterator = this.a.iterator();

                while (iterator.hasNext()) {
                    EntityTrackerEntry entitytrackerentry = iterator.next();

                    if (entitytrackerentry.tracker != entityplayer) {
                        entitytrackerentry.b(entityplayer);
                    }
                }
            }
            case EntityFish _ -> this.a(entity, 64, 5, true);
            case EntityArrow _ -> this.a(entity, 64, 20, false);
            case EntityFireball _ -> this.a(entity, 64, 10, false);
            case EntitySnowball _, EntityEgg _ -> this.a(entity, 64, 10, true);
            case EntityItem _ -> this.a(entity, 64, 20, true);
            case EntityMinecart _, EntityBoat _ -> this.a(entity, 160, 5, true);
            case EntitySquid _ -> this.a(entity, 160, 3, true);
            case IAnimal _ -> this.a(entity, 160, 3);
            case EntityTNTPrimed _ -> this.a(entity, 160, 10, true);
            case EntityFallingSand _ -> this.a(entity, 160, 20, true);
            case EntityPainting _ -> this.a(entity, 160, Integer.MAX_VALUE, false);
            default -> {}
        }
    }

    public void a(Entity entity, int i, int j) {
        this.a(entity, i, j, false);
    }

    // CraftBukkit - synchronized
    public synchronized void a(Entity entity, int i, int j, boolean flag) {
        if (i > this.d) {
            i = this.d;
        }

        if (this.b.b(entity.id)) {
            // CraftBukkit - removed exception throw as tracking an already tracked entity theoretically shouldn't cause any issues.
            // throw new IllegalStateException("Entity is already tracked!");
        } else {
            EntityTrackerEntry entitytrackerentry = new EntityTrackerEntry(entity, i, j, flag);

            this.a.add(entitytrackerentry);
            this.b.a(entity.id, entitytrackerentry);
            entitytrackerentry.scanPlayers(this.c.getWorldServer(this.e).players);
        }
    }

    // CraftBukkit - synchronized
    public synchronized void untrackEntity(Entity entity) {
        if (entity instanceof EntityPlayer entityplayer) {
            Iterator<EntityTrackerEntry> iterator = this.a.iterator();

            while (iterator.hasNext()) {
                EntityTrackerEntry entitytrackerentry = iterator.next();

                entitytrackerentry.a(entityplayer);
            }
        }

        EntityTrackerEntry entitytrackerentry1 = (EntityTrackerEntry) this.b.d(entity.id);

        if (entitytrackerentry1 != null) {
            this.a.remove(entitytrackerentry1);
            entitytrackerentry1.a();
        }
    }

    // CraftBukkit - synchronized
    public synchronized void updatePlayers() {
        ArrayList<EntityPlayer> arraylist = new ArrayList<>();
        Iterator<EntityTrackerEntry> iterator = this.a.iterator();

        while (iterator.hasNext()) {
            EntityTrackerEntry entitytrackerentry = iterator.next();

            entitytrackerentry.track(this.c.getWorldServer(this.e).players);
            if (entitytrackerentry.m && entitytrackerentry.tracker instanceof EntityPlayer) {
                arraylist.add((EntityPlayer) entitytrackerentry.tracker);
            }
        }

        for (int i = 0; i < arraylist.size(); ++i) {
            EntityPlayer entityplayer = arraylist.get(i);
            Iterator<EntityTrackerEntry> iterator1 = this.a.iterator();

            while (iterator1.hasNext()) {
                EntityTrackerEntry entitytrackerentry1 = iterator1.next();

                if (entitytrackerentry1.tracker != entityplayer) {
                    entitytrackerentry1.b(entityplayer);
                }
            }
        }
    }

    // CraftBukkit - synchronized
    public synchronized void a(Entity entity, Packet packet) {
        EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) this.b.a(entity.id);

        if (entitytrackerentry != null) {
            entitytrackerentry.a(packet);
        }
    }

    // CraftBukkit - synchronized
    public synchronized void sendPacketToEntity(Entity entity, Packet packet) {
        EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) this.b.a(entity.id);

        if (entitytrackerentry != null) {
            entitytrackerentry.b(packet);
        }
    }

    // CraftBukkit - synchronized
    public synchronized void untrackPlayer(EntityPlayer entityplayer) {
        Iterator<EntityTrackerEntry> iterator = this.a.iterator();

        while (iterator.hasNext()) {
            EntityTrackerEntry entitytrackerentry = iterator.next();

            entitytrackerentry.c(entityplayer);
        }
    }
}
