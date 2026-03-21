package net.minecraft.server;

import org.jspecify.annotations.Nullable;

public interface IChunkProvider {

    boolean isChunkLoaded(int i, int j);

    Chunk getOrCreateChunk(int i, int j);

    Chunk getChunkAt(int i, int j);

    void getChunkAt(IChunkProvider ichunkprovider, int i, int j);

    boolean saveChunks(boolean flag, @Nullable IProgressUpdate iprogressupdate);

    boolean unloadChunks();

    boolean canSave();
}
