package com.legacyminecraft.poseidon.util;

public final class ChunkPos {

    public static long of(int x, int z) {
        return ((x & 0xFFFFFFFFL) << 32) | (z & 0xFFFFFFFFL);
    }

    public static int x(long pos) {
        return (int) (pos >> 32);
    }

    public static int z(long pos) {
        return (int) (pos & 0xFFFFFFFFL);
    }
}
