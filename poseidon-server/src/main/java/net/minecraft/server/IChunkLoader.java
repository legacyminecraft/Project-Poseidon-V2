package net.minecraft.server;

import org.jspecify.annotations.Nullable;

import java.io.IOException;

public interface IChunkLoader {

    @Nullable Chunk a(World world, int i, int j) throws IOException;

    void a(World world, Chunk chunk);

    void b(World world, Chunk chunk);

    void a();

    void b();
}
