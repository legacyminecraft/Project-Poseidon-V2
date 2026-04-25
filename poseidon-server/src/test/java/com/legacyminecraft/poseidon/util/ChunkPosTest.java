package com.legacyminecraft.poseidon.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ChunkPosTest {

    @ParameterizedTest
    @MethodSource("arguments")
    void canPackAndUnpack(Arguments args) {
        int actualX = args.x();
        int actualZ = args.z();
        long pos = ChunkPos.of(actualX, actualZ);
        int unpackedX = ChunkPos.x(pos);
        int unpackedZ = ChunkPos.z(pos);

        assertThat(unpackedX).isEqualTo(actualX);
        assertThat(unpackedZ).isEqualTo(actualZ);
    }

    static Stream<Arguments> arguments() {
        return Stream.of(
                new Arguments(-40, 138),
                new Arguments(0, -1),
                new Arguments(65535, -32767)
        );
    }

    record Arguments(int x, int z) {
    }
}