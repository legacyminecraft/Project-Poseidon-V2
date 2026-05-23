package com.legacyminecraft.poseidon.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BlockPosTest {

    @ParameterizedTest
    @MethodSource("arguments")
    void canPackAndUnpack(int x, int y, int z) {
        long pos = BlockPos.of(x, y, z);
        int unpackedX = BlockPos.x(pos);
        int unpackedY = BlockPos.y(pos);
        int unpackedZ = BlockPos.z(pos);

        assertThat(unpackedX).isEqualTo(x);
        assertThat(unpackedY).isEqualTo(y);
        assertThat(unpackedZ).isEqualTo(z);
    }

    static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of(-40, 65, 138),
                Arguments.of(0, 127, 0),
                Arguments.of(1048575, 0, -524287)
        );
    }
}