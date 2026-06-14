package com.legacyminecraft.poseidon.world;

import net.minecraft.server.Block;
import net.minecraft.server.Chunk;
import net.minecraft.server.World;
import org.bukkit.Material;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class AntiXrayEngine {

    private final boolean enabled;
    private final World world;
    private final boolean[] obfuscateBlock = new boolean[Block.byId.length];
    private final byte[] replacementBlocks;
    private final int minSegmentSize;
    private final int maxSegmentSize;

    public AntiXrayEngine(World world) {
        this.world = world;
        this.enabled = world.getConfig().anticheat.antiXray.enabled;
        this.minSegmentSize = world.getConfig().anticheat.antiXray.minSegmentSize;
        this.maxSegmentSize = world.getConfig().anticheat.antiXray.maxSegmentSize;

        for (Material material : world.getConfig().anticheat.antiXray.obfuscatedBlocks) {
            if (material.getId() > 0 && material.getId() <= 96) {
                this.obfuscateBlock[material.getId()] = true;
            }
        }

        Set<Material> materials = new LinkedHashSet<>();
        for (Material material : world.getConfig().anticheat.antiXray.replacementBlocks) {
            if (material.getId() > 0 && material.getId() <= 96) {
                materials.add(material);
            }
        }

        this.replacementBlocks = new byte[materials.size()];
        int i = 0;
        for (Material material : materials) {
            this.replacementBlocks[i] = (byte) material.getId();
            i++;
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public byte[] createChunkSnapshot(int chunkX, int chunkZ) {
        byte[] snapshot = new byte[20 * 128 * 20];
        int index = 0;

        for (int x = -2; x < 18; x++) {
            for (int z = -2; z < 18; z++) {
                Chunk chunk = world.getChunkAt(chunkX + (x >> 4), chunkZ + (z >> 4));
                int relX = (x + 16) & 15;
                int relZ = (z + 16) & 15;
                for (int y = 0; y < 128; y++) {
                    snapshot[index] = (byte) chunk.getTypeId(relX, y, relZ);
                    index++;
                }
            }
        }

        return snapshot;
    }

    public void obfuscate(byte[] chunk, byte[] snapshot) {
        int index = 0;
        int segment = 0;
        byte replacementBlock = 1;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 128; y++) {
                    int type = chunk[index];
                    if (this.obfuscateBlock[type] && !hasTransparentBlockAdjacent(snapshot, x, y, z)) {
                        if (segment == 0) {
                            segment = ThreadLocalRandom.current().nextInt(this.minSegmentSize, this.maxSegmentSize + 1);
                            replacementBlock = generateRandomBlock();
                        }
                        setBlock(chunk, index, replacementBlock);
                        segment--;
                    }
                    index++;
                }
            }
        }
    }

    private boolean hasTransparentBlockAdjacent(byte[] snapshot, int x, int y, int z) {
        return isTransparentBlock(snapshot, x - 1, y, z)
                || isTransparentBlock(snapshot, x + 1, y, z)
                || isTransparentBlock(snapshot, x, y - 1, z)
                || isTransparentBlock(snapshot, x, y + 1, z)
                || isTransparentBlock(snapshot, x, y, z - 1)
                || isTransparentBlock(snapshot, x, y, z + 1)
                || isTransparentBlock(snapshot, x - 1, y - 1, z)
                || isTransparentBlock(snapshot, x - 1, y + 1, z)
                || isTransparentBlock(snapshot, x - 1, y, z - 1)
                || isTransparentBlock(snapshot, x - 1, y, z + 1)
                || isTransparentBlock(snapshot, x + 1, y - 1, z)
                || isTransparentBlock(snapshot, x + 1, y + 1, z)
                || isTransparentBlock(snapshot, x + 1, y, z - 1)
                || isTransparentBlock(snapshot, x + 1, y, z + 1)
                || isTransparentBlock(snapshot, x, y - 1, z - 1)
                || isTransparentBlock(snapshot, x, y - 1, z + 1)
                || isTransparentBlock(snapshot, x, y + 1, z - 1)
                || isTransparentBlock(snapshot, x, y + 1, z + 1)
                || isTransparentBlock(snapshot, x - 2, y, z)
                || isTransparentBlock(snapshot, x + 2, y, z)
                || isTransparentBlock(snapshot, x, y - 2, z)
                || isTransparentBlock(snapshot, x, y + 2, z)
                || isTransparentBlock(snapshot, x, y, z - 2)
                || isTransparentBlock(snapshot, x, y, z + 2);
    }

    private boolean isTransparentBlock(byte[] snapshot, int x, int y, int z) {
        return !Block.o[getSnapshotType(snapshot, x, y, z)];
    }

    private byte getSnapshotType(byte[] snapshot, int x, int y, int z) {
        return snapshot[((x + 2) * 2560) + ((z + 2) * 128) + y];
    }

    private void setBlock(byte[] chunk, int typeIndex, byte type) {
        chunk[typeIndex] = type;
        int dataIndex = (chunk.length * 2 / 5) + (typeIndex >> 1);
        int nibble = typeIndex & 1;
        if (nibble == 0) {
            chunk[dataIndex] = (byte) (chunk[dataIndex] & 240);
        } else {
            chunk[dataIndex] = (byte) (chunk[dataIndex] & 15);
        }
    }

    private byte generateRandomBlock() {
        if (this.replacementBlocks.length == 0) {
            return (byte) Block.STONE.id;
        } else {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            return this.replacementBlocks[random.nextInt(this.replacementBlocks.length)];
        }
    }

    public void updateNearbyBlocks(int x, int y, int z) {
        updateBlock(x - 1, y, z);
        updateBlock(x + 1, y, z);
        updateBlock(x, y - 1, z);
        updateBlock(x, y + 1, z);
        updateBlock(x, y, z - 1);
        updateBlock(x, y, z + 1);
        updateBlock(x - 1, y - 1, z);
        updateBlock(x - 1, y + 1, z);
        updateBlock(x - 1, y, z - 1);
        updateBlock(x - 1, y, z + 1);
        updateBlock(x + 1, y - 1, z);
        updateBlock(x + 1, y + 1, z);
        updateBlock(x + 1, y, z - 1);
        updateBlock(x + 1, y, z + 1);
        updateBlock(x, y - 1, z - 1);
        updateBlock(x, y - 1, z + 1);
        updateBlock(x, y + 1, z - 1);
        updateBlock(x, y + 1, z + 1);
        updateBlock(x - 2, y, z);
        updateBlock(x + 2, y, z);
        updateBlock(x, y - 2, z);
        updateBlock(x, y + 2, z);
        updateBlock(x, y, z - 2);
        updateBlock(x, y, z + 2);
    }

    private void updateBlock(int x, int y, int z) {
        if (this.obfuscateBlock[this.world.getTypeId(x, y, z)]) {
            this.world.notify(x, y, z);
        }
    }

    public boolean areAdjacentChunksLoaded(int chunkX, int chunkZ) {
        return isChunkLoaded(chunkX - 1, chunkZ)
                && isChunkLoaded(chunkX + 1, chunkZ)
                && isChunkLoaded(chunkX, chunkZ - 1)
                && isChunkLoaded(chunkX, chunkZ + 1)
                && isChunkLoaded(chunkX - 1, chunkZ - 1)
                && isChunkLoaded(chunkX - 1, chunkZ + 1)
                && isChunkLoaded(chunkX + 1, chunkZ - 1)
                && isChunkLoaded(chunkX + 1, chunkZ + 1);
    }

    private boolean isChunkLoaded(int chunkX, int chunkZ) {
        return this.world.getChunkAt(chunkX, chunkZ).done;
    }
}
