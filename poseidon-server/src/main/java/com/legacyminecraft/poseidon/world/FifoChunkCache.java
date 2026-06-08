package com.legacyminecraft.poseidon.world;

import com.legacyminecraft.poseidon.util.ChunkPos;
import net.minecraft.server.Chunk;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;

public final class FifoChunkCache {

    private static final long EMPTY_CHUNK_POS = Long.MIN_VALUE;

    private final long[] chunkPosCache;
    private final Chunk[] chunkCache;

    public FifoChunkCache(int size) {
        this.chunkPosCache = new long[size];
        this.chunkCache = new Chunk[size];
        Arrays.fill(this.chunkPosCache, EMPTY_CHUNK_POS);
    }

    public @Nullable Chunk getChunk(int x, int z) {
        long chunkPos = ChunkPos.of(x, z);
        for (int i = 0; i < this.chunkPosCache.length; i++) {
            if (chunkPos == this.chunkPosCache[i]) {
                return this.chunkCache[i];
            }
        }
        return null;
    }

    public void storeChunk(int x, int z, Chunk chunk) {
        for (int i = this.chunkPosCache.length - 1; i > 0; i--) {
            this.chunkPosCache[i] = this.chunkPosCache[i - 1];
            this.chunkCache[i] = this.chunkCache[i - 1];
        }

        this.chunkPosCache[0] = ChunkPos.of(x, z);
        this.chunkCache[0] = chunk;
    }

    public void clear() {
        Arrays.fill(this.chunkPosCache, EMPTY_CHUNK_POS);
        Arrays.fill(this.chunkCache, null);
    }
}
