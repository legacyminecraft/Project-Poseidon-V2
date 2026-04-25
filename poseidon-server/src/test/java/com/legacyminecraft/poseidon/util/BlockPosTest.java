package com.legacyminecraft.poseidon.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BlockPosTest {

    @ParameterizedTest
    @MethodSource("arguments")
    void canPackAndUnpack(Arguments args) {
        int actualX = args.x();
        int actualY = args.y();
        int actualZ = args.z();
        long pos = BlockPos.of(actualX, actualY, actualZ);
        int unpackedX = BlockPos.x(pos);
        int unpackedY = BlockPos.y(pos);
        int unpackedZ = BlockPos.z(pos);

        assertThat(unpackedX).isEqualTo(actualX);
        assertThat(unpackedY).isEqualTo(actualY);
        assertThat(unpackedZ).isEqualTo(actualZ);
    }

    static Stream<Arguments> arguments() {
        return Stream.of(
                new Arguments(-40, 65, 138),
                new Arguments(0, 127, 0),
                new Arguments(1048575, 0, -524287)
        );
    }

    record Arguments(int x, int y, int z) {
    }
}