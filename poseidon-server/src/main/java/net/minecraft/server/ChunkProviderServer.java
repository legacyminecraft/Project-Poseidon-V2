package net.minecraft.server;

import com.legacyminecraft.poseidon.util.ChunkPos;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.generator.BlockPopulator;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class ChunkProviderServer implements IChunkProvider {

    private static final Logger log = LoggerFactory.getLogger(ChunkProviderServer.class); // Poseidon

    // CraftBukkit start
    public LongLinkedOpenHashSet unloadQueue = new LongLinkedOpenHashSet(); // Poseidon - LongHashset -> LongLinkedOpenHashSet
    public Chunk emptyChunk;
    public IChunkProvider chunkProvider; // CraftBukkit
    private IChunkLoader e;
    public boolean forceChunkLoad = false;
    public Long2ObjectOpenHashMap<Chunk> chunks = new Long2ObjectOpenHashMap<>(); // Poseidon - LongHashtable -> Long2ObjectOpenHashMap
    //public List<Chunk> chunkList = new ArrayList<>(); // Poseidon - remove
    public WorldServer world;
    // CraftBukkit end

    public ChunkProviderServer(WorldServer worldserver, IChunkLoader ichunkloader, IChunkProvider ichunkprovider) {
        this.emptyChunk = new EmptyChunk(worldserver, new byte['\u8000'], 0, 0);
        this.world = worldserver;
        this.e = ichunkloader;
        this.chunkProvider = ichunkprovider;
    }

    public boolean isChunkLoaded(int i, int j) {
        return this.chunks.containsKey(ChunkPos.of(i, j)); // CraftBukkit // Poseidon
    }

    public void queueUnload(int i, int j) {
        ChunkCoordinates chunkcoordinates = this.world.getSpawn();
        int k = i * 16 + 8 - chunkcoordinates.x;
        int l = j * 16 + 8 - chunkcoordinates.z;
        short short1 = 128;

        if (k < -short1 || k > short1 || l < -short1 || l > short1 || !(this.world.keepSpawnInMemory)) { // CraftBukkit - added 'this.world.keepSpawnInMemory'
            this.unloadQueue.add(ChunkPos.of(i, j)); // CraftBukkit // Poseidon
        }
    }

    public Chunk getChunkAt(int i, int j) {
        // CraftBukkit start
        this.unloadQueue.remove(ChunkPos.of(i, j)); // Poseidon
        Chunk chunk = this.chunks.get(ChunkPos.of(i, j)); // Poseidon
        boolean newChunk = false;
        // CraftBukkit end

        if (chunk == null) {
            chunk = this.loadChunk(i, j);
            if (chunk == null) {
                if (this.chunkProvider == null) {
                    chunk = this.emptyChunk;
                } else {
                    chunk = this.chunkProvider.getOrCreateChunk(i, j);
                }
                newChunk = true; // CraftBukkit
            }

            this.chunks.put(ChunkPos.of(i, j), chunk); // CraftBukkit // Poseidon
            //this.chunkList.add(chunk); // Poseidon
            if (chunk != null) {
                chunk.loadNOP();
                chunk.addEntities();
            }

            // CraftBukkit start
            org.bukkit.Server server = this.world.getServer();
            if (server != null) {
                /*
                 * If it's a new world, the first few chunks are generated inside
                 * the World constructor. We can't reliably alter that, so we have
                 * no way of creating a CraftWorld/CraftServer at that point.
                 */
                server.getPluginManager().callEvent(new ChunkLoadEvent(chunk.bukkitChunk, newChunk));
            }
            // CraftBukkit end

            if (!chunk.done && this.isChunkLoaded(i + 1, j + 1) && this.isChunkLoaded(i, j + 1) && this.isChunkLoaded(i + 1, j)) {
                this.getChunkAt(this, i, j);
            }

            if (this.isChunkLoaded(i - 1, j) && !this.getOrCreateChunk(i - 1, j).done && this.isChunkLoaded(i - 1, j + 1) && this.isChunkLoaded(i, j + 1) && this.isChunkLoaded(i - 1, j)) {
                this.getChunkAt(this, i - 1, j);
            }

            if (this.isChunkLoaded(i, j - 1) && !this.getOrCreateChunk(i, j - 1).done && this.isChunkLoaded(i + 1, j - 1) && this.isChunkLoaded(i, j - 1) && this.isChunkLoaded(i + 1, j)) {
                this.getChunkAt(this, i, j - 1);
            }

            if (this.isChunkLoaded(i - 1, j - 1) && !this.getOrCreateChunk(i - 1, j - 1).done && this.isChunkLoaded(i - 1, j - 1) && this.isChunkLoaded(i, j - 1) && this.isChunkLoaded(i - 1, j)) {
                this.getChunkAt(this, i - 1, j - 1);
            }
        }

        return chunk;
    }

    public Chunk getOrCreateChunk(int i, int j) {
        // CraftBukkit start
        Chunk chunk = this.chunks.get(ChunkPos.of(i, j)); // Poseidon

        try {
            chunk = chunk == null ? (!this.world.isLoading && !this.forceChunkLoad ? this.emptyChunk : this.getChunkAt(i, j)) : chunk;
        } catch (Exception e) {
            // Poseidon start - regenerate corrupt chunks
            log.error("Error occurred while loading chunk ({}, {})", i, j, e);
            if (this.world.getConfig().chunks.regenerateCorruptChunks) {
                log.info("Regenerating chunk ({}, {})", i, j);
                chunk = this.emptyChunk;
            }
            // Poseidon end
        }

        if (chunk == this.emptyChunk) return chunk;
        if (i != chunk.x || j != chunk.z) {
            // Poseidon - use slf4j logger
            log.error("Chunk ({}, {}) stored at ({}, {})", chunk.x, chunk.z, i, j);
            Throwable ex = new Throwable();
            ex.fillInStackTrace();
            ex.printStackTrace();
        }
        return chunk;
        // CraftBukkit end
    }

    public @Nullable Chunk loadChunk(int i, int j) { // CraftBukkit - private -> public
        if (this.e == null) {
            return null;
        } else {
            try {
                Chunk chunk = this.e.a(this.world, i, j);

                if (chunk != null) {
                    chunk.r = this.world.getTime();
                }

                return chunk;
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }
    }

    public void saveChunkNOP(Chunk chunk) { // CraftBukkit - private -> public
        if (this.e != null) {
            try {
                this.e.b(this.world, chunk);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void saveChunk(Chunk chunk) { // CraftBukkit - private -> public
        if (this.e != null) {
            try {
                chunk.r = this.world.getTime();
                this.e.a(this.world, chunk);
            } catch (Exception ioexception) { // CraftBukkit - IOException -> Exception
                ioexception.printStackTrace();
            }
        }
    }

    public void getChunkAt(IChunkProvider ichunkprovider, int i, int j) {
        Chunk chunk = this.getOrCreateChunk(i, j);

        if (!chunk.done) {
            chunk.done = true;
            if (this.chunkProvider != null) {
                this.chunkProvider.getChunkAt(ichunkprovider, i, j);

                // CraftBukkit start
                BlockSand.instaFall = true;
                Random random = new Random();
                random.setSeed(world.getSeed());
                long xRand = random.nextLong() / 2L * 2L + 1L;
                long zRand = random.nextLong() / 2L * 2L + 1L;
                random.setSeed((long) i * xRand + (long) j * zRand ^ world.getSeed());

                org.bukkit.World world = this.world.getWorld();
                if (world != null) {
                    for (BlockPopulator populator : world.getPopulators()) {
                        populator.populate(world, random, chunk.bukkitChunk);
                    }
                }
                BlockSand.instaFall = false;
                this.world.getServer().getPluginManager().callEvent(new ChunkPopulateEvent(chunk.bukkitChunk));
                // CraftBukkit end

                chunk.f();
            }
        }
    }

    public boolean saveChunks(boolean flag, @Nullable IProgressUpdate iprogressupdate) {
        int i = 0;

        // Poseidon - iterate directly over values
        for (Chunk chunk : this.chunks.values()) {
            if (flag && !chunk.p) {
                this.saveChunkNOP(chunk);
            }

            if (chunk.a(flag)) {
                this.saveChunk(chunk);
                chunk.o = false;
                ++i;
                if (i == 24 && !flag) {
                    return false;
                }
            }
        }

        if (flag) {
            if (this.e == null) {
                return true;
            }

            this.e.b();
        }

        return true;
    }

    public boolean unloadChunks() {
        if (!this.world.canSave) {
            // CraftBukkit start
            org.bukkit.Server server = this.world.getServer();
            for (int i = 0; i < 50 && !this.unloadQueue.isEmpty(); i++) {
                long chunkcoordinates = this.unloadQueue.removeFirstLong(); // Poseidon
                Chunk chunk = this.chunks.get(chunkcoordinates);
                if (chunk == null) continue;

                ChunkUnloadEvent event = new ChunkUnloadEvent(chunk.bukkitChunk);
                server.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    chunk.removeEntities();
                    this.saveChunk(chunk);
                    this.saveChunkNOP(chunk);
                    // this.unloadQueue.remove(integer);
                    this.chunks.remove(chunkcoordinates); // CraftBukkit
                    //this.chunkList.remove(chunk); // Poseidon
                }
            }
            // CraftBukkit end

            if (this.e != null) {
                this.e.a();
            }
        }

        return this.chunkProvider.unloadChunks();
    }

    public boolean canSave() {
        return !this.world.canSave;
    }
}
