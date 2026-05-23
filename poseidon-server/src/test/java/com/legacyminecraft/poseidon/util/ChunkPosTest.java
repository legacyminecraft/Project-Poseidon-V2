package com.legacyminecraft.poseidon.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ChunkPosTest {

    @ParameterizedTest
    @MethodSource("arguments")
    void canPackAndUnpack(int x, int z) {
        long pos = ChunkPos.of(x, z);
        int unpackedX = ChunkPos.x(pos);
        int unpackedZ = ChunkPos.z(pos);

        assertThat(unpackedX).isEqualTo(x);
        assertThat(unpackedZ).isEqualTo(z);
    }

    static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of(-40, 138),
                Arguments.of(0, -1),
                Arguments.of(65535, -32767)
        );
    }
}