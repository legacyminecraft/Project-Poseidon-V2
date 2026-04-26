package com.legacyminecraft.poseidon.world;

import net.minecraft.server.Block;
import net.minecraft.server.Chunk;

public final class ChunkSection {

    private final Chunk chunk;
    private final int yPos;

    private int nonEmptyBlocks = 0;
    private int tickableBlocks = 0;

    public ChunkSection(Chunk chunk, int yPos) {
        this.chunk = chunk;
        this.yPos = yPos;
    }

    public Chunk getChunk() {
        return this.chunk;
    }

    public int getY() {
        return this.yPos;
    }

    public boolean hasBlocks() {
        return this.nonEmptyBlocks > 0;
    }

    public boolean hasTickableBlocks() {
        return this.tickableBlocks > 0;
    }

    public void calculateBlockCounts() {
        this.nonEmptyBlocks = 0;
        this.tickableBlocks = 0;

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    int type = this.chunk.getTypeId(x, (this.yPos << 4) + y, z);
                    if (type != 0) {
                        ++this.nonEmptyBlocks;
                        if (Block.n[type]) {
                            ++this.tickableBlocks;
                        }
                    }
                }
            }
        }
    }

    public void update(int oldType, int newType) {
        if (Block.n[oldType]) {
            if (!Block.n[newType]) {
                --this.tickableBlocks;
            }
        } else {
            if (Block.n[newType]) {
                ++this.tickableBlocks;
            }
        }

        if (oldType != 0) {
            if (newType == 0) {
                --this.nonEmptyBlocks;
            }
        } else {
            if (newType != 0) {
                ++this.nonEmptyBlocks;
            }
        }
    }
}
