package com.legacyminecraft.poseidon.util;

public final class BlockPos {

    public static long of(int x, int y, int z) {
        return ((x & 0xFFFFFFFL) << 36) | ((z & 0xFFFFFFFL) << 8) | (y & 0xFFL);
    }

    public static int x(long pos) {
        return (int) (pos >> 36);
    }

    public static int y(long pos) {
        return (int) (pos << 56 >> 56);
    }

    public static int z(long pos) {
        return (int) (pos << 28 >> 36);
    }
}
