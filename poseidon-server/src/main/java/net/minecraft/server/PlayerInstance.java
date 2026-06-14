package net.minecraft.server;

import com.legacyminecraft.poseidon.util.ChunkPos;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jspecify.annotations.Nullable;

class PlayerInstance {

    private ObjectOpenHashSet<EntityPlayer> b; // Poseidon - List -> ObjectOpenHashSet
    private int chunkX;
    private int chunkZ;
    private long location; // Poseidon - ChunkCoordIntPair -> long
    private short[] dirtyBlocks;
    private int dirtyCount;
    // Poseidon start - change update notification algorithm
    /*private int h;
    private int i;
    private int j;
    private int k;
    private int l;
    private int m;*/
    // Poseidon end

    final PlayerManager playerManager;

    public PlayerInstance(PlayerManager playermanager, int i, int j) {
        this.playerManager = playermanager;
        this.b = new ObjectOpenHashSet<>(); // Poseidon - ArrayList -> ObjectOpenHashSet
        this.dirtyBlocks = new short[64]; // Poseidon - 10 -> 64
        this.dirtyCount = 0;
        this.chunkX = i;
        this.chunkZ = j;
        this.location = ChunkPos.of(i, j); // Poseidon
        playermanager.a().chunkProviderServer.getChunkAt(i, j);
    }

    public void a(EntityPlayer entityplayer) {
        if (!this.b.add(entityplayer)) { // Poseidon - remove redundant contains check
            throw new IllegalStateException("Failed to add player. " + entityplayer + " already is in chunk " + this.chunkX + ", " + this.chunkZ);
        } else {
            // CraftBukkit start
            if (entityplayer.playerChunkCoordIntPairs.add(this.location)) {
                entityplayer.netServerHandler.sendPacket(new Packet50PreChunk(this.chunkX, this.chunkZ, true)); // Poseidon
            }
            // CraftBukkit end

            entityplayer.chunkCoordIntPairQueue.add(this.location);
        }
    }

    public void b(EntityPlayer entityplayer) {
        if (this.b.remove(entityplayer)) { // Poseidon - remove redundant contains check
            if (this.b.isEmpty()) {
                PlayerManager.a(this.playerManager).remove(this.location); // Poseidon

                if (this.dirtyCount > 0) {
                    PlayerManager.b(this.playerManager).remove(this);
                }

                this.playerManager.a().chunkProviderServer.queueUnload(this.chunkX, this.chunkZ);
            }

            entityplayer.chunkCoordIntPairQueue.rem(this.location); // Poseidon - remove -> rem
            if (entityplayer.playerChunkCoordIntPairs.remove(this.location)) {
                entityplayer.netServerHandler.sendPacket(new Packet50PreChunk(this.chunkX, this.chunkZ, false));
            }
        }
    }

    public void a(int i, int j, int k) {
        if (this.dirtyCount == 0) {
            PlayerManager.b(this.playerManager).add(this);
            // Poseidon start - change update notification algorithm
            /*this.h = this.i = i;
            this.j = this.k = j;
            this.l = this.m = k;*/
        }

        /*if (this.h > i) {
            this.h = i;
        }

        if (this.i < i) {
            this.i = i;
        }

        if (this.j > j) {
            this.j = j;
        }

        if (this.k < j) {
            this.k = j;
        }

        if (this.l > k) {
            this.l = k;
        }

        if (this.m < k) {
            this.m = k;
        }*/

        if (this.dirtyCount < this.dirtyBlocks.length) {
            // Poseidon end
            short short1 = (short) (i << 12 | k << 8 | j);

            for (int l = 0; l < this.dirtyCount; ++l) {
                if (this.dirtyBlocks[l] == short1) {
                    return;
                }
            }

            this.dirtyBlocks[this.dirtyCount++] = short1;
        }
    }

    public void sendAll(Packet packet) {
        this.b.forEach(entityplayer -> { // Poseidon - forEach
            if (entityplayer.playerChunkCoordIntPairs.contains(this.location)) {
                entityplayer.netServerHandler.sendPacket(packet);
            }
        });
    }

    public void a() {
        WorldServer worldserver = this.playerManager.a();

        if (this.dirtyCount != 0) {
            int i;
            int j;
            int k;

            if (this.dirtyCount == 1) {
                // Poseidon start - change update notification algorithm
                i = this.chunkX * 16 + (this.dirtyBlocks[0] >> 12 & 15);
                j = this.dirtyBlocks[0] & 255;
                k = this.chunkZ * 16 + (this.dirtyBlocks[0] >> 8 & 15);
                // Poseidon end
                this.sendAll(new Packet53BlockChange(i, j, k, worldserver));
                if (Block.isTileEntity[worldserver.getTypeId(i, j, k)]) {
                    this.sendTileEntity(worldserver.getTileEntity(i, j, k));
                }
            } else {
                int l;

                // Poseidon start - change update notification algorithm
                if (this.dirtyCount == this.dirtyBlocks.length) {
                    /*this.j = this.j / 2 * 2;
                    this.k = (this.k / 2 + 1) * 2;
                    i = this.h + this.chunkX * 16;
                    j = this.j;
                    k = this.l + this.chunkZ * 16;
                    l = this.i - this.h + 1;
                    int i1 = this.k - this.j + 2;
                    int j1 = this.m - this.l + 1;*/

                    this.b.forEach(entityplayer -> entityplayer.chunkCoordIntPairQueue.add(this.location));
                    // Poseidon end
                } else {
                    this.sendAll(new Packet52MultiBlockChange(this.chunkX, this.chunkZ, this.dirtyBlocks, this.dirtyCount, worldserver));

                    for (i = 0; i < this.dirtyCount; ++i) {
                        // CraftBukkit start - Fixes TileEntity updates occurring upon a multi-block change; dirtyCount -> dirtyBlocks[i]
                        j = this.chunkX * 16 + (this.dirtyBlocks[i] >> 12 & 15);
                        k = this.dirtyBlocks[i] & 255;
                        l = this.chunkZ * 16 + (this.dirtyBlocks[i] >> 8 & 15);
                        // CraftBukkit end

                        if (Block.isTileEntity[worldserver.getTypeId(j, k, l)]) {
                            // System.out.println("Sending!"); // CraftBukkit
                            this.sendTileEntity(worldserver.getTileEntity(j, k, l));
                        }
                    }
                }
            }

            this.dirtyCount = 0;
        }
    }

    private void sendTileEntity(@Nullable TileEntity tileentity) {
        if (tileentity != null) {
            Packet packet = tileentity.f();

            if (packet != null) {
                this.sendAll(packet);
            }
        }
    }

    // Poseidon start
    static long a(PlayerInstance playerchunk) {
        return playerchunk.location;
    }

    static ObjectOpenHashSet<EntityPlayer> b(PlayerInstance playerchunk) {
        return playerchunk.b;
    }
    // Poseidon end
}
