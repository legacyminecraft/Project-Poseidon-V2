package com.legacyminecraft.poseidon.world;

import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.WorldServer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class FifoChunkCacheTest {

    @ParameterizedTest
    @MethodSource("arguments")
    void canStoreAndRetrieve(List<ChunkCoordIntPair> pairs) {
        FifoChunkCache chunkCache = new FifoChunkCache(pairs.size());
        List<ChunkCoordIntPair> storedPairs = new ArrayList<>();
        for (ChunkCoordIntPair pair : pairs) {
            chunkCache.storeChunk(pair.x, pair.z, new Chunk(mock(WorldServer.class), pair.x, pair.z));
            storedPairs.add(pair);
            assertThat(storedPairs).allSatisfy(stored -> {
                Chunk chunk = chunkCache.getChunk(stored.x, stored.z);
                assertThat(chunk).isNotNull();
                assertThat(chunk.x).isEqualTo(stored.x);
                assertThat(chunk.z).isEqualTo(stored.z);
            });
        }
    }

    static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                new ChunkCoordIntPair(-3990, -8320),
                                new ChunkCoordIntPair(20555, 27281)
                        ),
                        List.of(
                                new ChunkCoordIntPair(25749, -2494),
                                new ChunkCoordIntPair(-16197, -30609),
                                new ChunkCoordIntPair(-14179, 5458)
                        ),
                        List.of(
                                new ChunkCoordIntPair(4687, -8254),
                                new ChunkCoordIntPair(-14292, 21331),
                                new ChunkCoordIntPair(-26251, 6012),
                                new ChunkCoordIntPair(19264, -16945)
                        )
                )
        );
    }
}