package net.minecraft.server;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class EntityTracker {

    private final Int2ObjectOpenHashMap<EntityTrackerEntry> entries = new Int2ObjectOpenHashMap<>(); // Poseidon

    //private Set<EntityTrackerEntry> a = new HashSet<>(); // Poseidon - remove
    //private EntityList b = new EntityList(); // Poseidon - remove
    private MinecraftServer c;
    private int d;
    private int e;

    public EntityTracker(MinecraftServer minecraftserver, int i) {
        this.c = minecraftserver;
        this.e = i;
        this.d = minecraftserver.serverConfigurationManager.a();
    }

    public void track(Entity entity) {
        switch (entity) {
            case EntityPlayer entityplayer -> {
                this.a(entity, 512, 2);

                this.entries.values().forEach(entitytrackerentry -> { // Poseidon - forEach
                    if (entitytrackerentry.tracker != entityplayer) {
                        entitytrackerentry.b(entityplayer);
                    }
                });
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

    public void a(Entity entity, int i, int j, boolean flag) {
        if (i > this.d) {
            i = this.d;
        }

        // Poseidon start
        EntityTrackerEntry entitytrackerentry = new EntityTrackerEntry(entity, i, j, flag);
        if (this.entries.putIfAbsent(entity.id, entitytrackerentry) == null) {
            entitytrackerentry.scanPlayers(this.c.getWorldServer(this.e).players);
        }
        // Poseidon end
    }

    public void untrackEntity(Entity entity) {
        if (entity instanceof EntityPlayer entityplayer) {
            this.entries.values().forEach(entitytrackerentry -> entitytrackerentry.a(entityplayer)); // Poseidon - forEach
        }

        // Poseidon start
        EntityTrackerEntry entitytrackerentry1 = this.entries.remove(entity.id);
        if (entitytrackerentry1 != null) {
            entitytrackerentry1.a();
        }
        // Poseidon end
    }

    public void updatePlayers() {
        ObjectArrayList<EntityPlayer> arraylist = new ObjectArrayList<>(); // Poseidon - ObjectArrayList

        this.entries.values().forEach(entitytrackerentry -> { // Poseidon - forEach
            entitytrackerentry.track(this.c.getWorldServer(this.e).players);
            if (entitytrackerentry.m && entitytrackerentry.tracker instanceof EntityPlayer entityplayer) {
                arraylist.add(entityplayer);
            }
        });

        arraylist.forEach(entityplayer -> { // Poseidon - forEach
            this.entries.values().forEach(entitytrackerentry1 -> { // Poseidon - forEach
                if (entitytrackerentry1.tracker != entityplayer) {
                    entitytrackerentry1.b(entityplayer);
                }
            });
        });
    }

    public void a(Entity entity, Packet packet) {
        EntityTrackerEntry entitytrackerentry = this.entries.get(entity.id); // Poseidon

        if (entitytrackerentry != null) {
            entitytrackerentry.a(packet);
        }
    }

    public void sendPacketToEntity(Entity entity, Packet packet) {
        EntityTrackerEntry entitytrackerentry = this.entries.get(entity.id); // Poseidon

        if (entitytrackerentry != null) {
            entitytrackerentry.b(packet);
        }
    }

    public void untrackPlayer(EntityPlayer entityplayer) {
        this.entries.values().forEach(entitytrackerentry -> entitytrackerentry.c(entityplayer)); // Poseidon - forEach
    }

    // Poseidon start
    public void a(EntityPlayer entityplayer, Chunk chunk) {
        this.entries.values().forEach(entitytrackerentry -> {
            if (entitytrackerentry.tracker != entityplayer
                    && entitytrackerentry.tracker.bH == chunk.x
                    && entitytrackerentry.tracker.bJ == chunk.z) {
                entitytrackerentry.b(entityplayer);
            }
        });
    }
    // Poseidon end
}
